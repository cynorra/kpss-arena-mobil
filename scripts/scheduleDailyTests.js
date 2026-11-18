// Firestore'daki dailyTests koleksiyonuna dengeli KPSS testleri planlar.
// Kullanim: cd scripts; node scheduleDailyTests.js [--days=180] [--apply]

const admin = require('firebase-admin');
const path = require('path');

const serviceAccountPath = process.env.SERVICE_ACCOUNT_PATH || path.join(__dirname, 'serviceAccountKey.json');
const apply = process.argv.includes('--apply');
const daysArg = process.argv.find((a) => a.startsWith('--days='));
const TOTAL_DAYS = daysArg ? parseInt(daysArg.split('=')[1], 10) : 180;

admin.initializeApp({ credential: admin.credential.cert(require(serviceAccountPath)) });
const db = admin.firestore();

function pseudoShuffle(array, seed) {
  const result = [...array];
  let s = seed;
  for (let i = result.length - 1; i > 0; i--) {
    s = (s * 9301 + 49297) % 233280;
    const j = Math.floor((s / 233280) * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  return result;
}

function formatDate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

async function main() {
  console.log('QuestionPool yükleniyor...');
  const poolSnap = await db.collection('QuestionPool').get();
  console.log(`Toplam soru sayısı: ${poolSnap.size}`);

  const byCategory = {
    'Türkçe': [],
    'Matematik': [],
    'Tarih': [],
    'Coğrafya': [],
    'Vatandaşlık': [],
    'Güncel Bilgiler': [],
  };

  poolSnap.forEach((doc) => {
    const data = doc.data();
    const cat = data.category;
    if (byCategory[cat]) {
      byCategory[cat].push({
        id: doc.id,
        text: data.text,
        optionA: data.optionA,
        optionB: data.optionB,
        optionC: data.optionC,
        optionD: data.optionD,
        correct: data.correct,
        category: data.category,
        explanation: data.explanation || '',
      });
    }
  });

  for (const [cat, list] of Object.entries(byCategory)) {
    console.log(`- ${cat}: ${list.length} soru`);
  }

  // Vatandaslik + Guncel Bilgiler kombinasyonu (toplam 1 soru)
  const civicsPool = [...byCategory['Vatandaşlık'], ...byCategory['Güncel Bilgiler']];

  console.log(`\n${TOTAL_DAYS} gün için dengeli günlük testler planlanacak...`);
  console.log('Günlük Dağılım: 3 Türkçe, 3 Matematik, 3 Tarih, 2 Coğrafya, 1 Vatandaşlık/Güncel (Toplam 12)');

  // Bugünden başla (veya dünden itibaren başla ki bugünün testi kesin var olsun)
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 1); // Dünden başla

  const plannedTests = [];

  for (let dayOffset = 0; dayOffset < TOTAL_DAYS; dayOffset++) {
    const currentDate = new Date(startDate.getTime() + dayOffset * 86400000);
    const dateStr = formatDate(currentDate);

    // Kategori rotasyon indeksleri
    const turkceShuffled = pseudoShuffle(byCategory['Türkçe'], dayOffset * 17 + 1);
    const matShuffled = pseudoShuffle(byCategory['Matematik'], dayOffset * 23 + 2);
    const tarihShuffled = pseudoShuffle(byCategory['Tarih'], dayOffset * 31 + 3);
    const cogShuffled = pseudoShuffle(byCategory['Coğrafya'], dayOffset * 41 + 4);
    const civicsShuffled = pseudoShuffle(civicsPool, dayOffset * 47 + 5);

    const questions = [
      ...turkceShuffled.slice(0, 3),
      ...matShuffled.slice(0, 3),
      ...tarihShuffled.slice(0, 3),
      ...cogShuffled.slice(0, 2),
      ...civicsShuffled.slice(0, 1),
    ];

    if (questions.length !== 12) {
      console.error(`${dateStr} için 12 soru oluşturulamadı! Oluşan: ${questions.length}`);
      process.exit(1);
    }

    plannedTests.push({
      dateStr,
      publishedAt: currentDate.getTime(),
      active: true,
      questions,
    });
  }

  console.log(`Hazırlandı: ${plannedTests.length} günlük test (örnek: ${plannedTests[0].dateStr} - ${plannedTests[plannedTests.length - 1].dateStr})`);

  if (!apply) {
    console.log('\n[PROVA MODU] Uygulamak için komuta --apply parametresi ekleyin:');
    console.log('node scheduleDailyTests.js --apply');
    return;
  }

  console.log('\nFirestore dailyTests koleksiyonuna yazılıyor...');
  const BATCH_SIZE = 100;
  for (let i = 0; i < plannedTests.length; i += BATCH_SIZE) {
    const chunk = plannedTests.slice(i, i + BATCH_SIZE);
    const batch = db.batch();

    for (const test of chunk) {
      const docRef = db.collection('dailyTests').doc(test.dateStr);
      batch.set(docRef, {
        active: test.active,
        publishedAt: test.publishedAt,
        questions: test.questions,
      });
    }

    await batch.commit();
    console.log(`- ${Math.min(i + BATCH_SIZE, plannedTests.length)} / ${plannedTests.length} gün yazıldı.`);
  }

  console.log('Tüm günlük testler başarıyla oluşturuldu ve yüklendi!');
}

main().catch((err) => {
  console.error('Hata:', err);
  process.exit(1);
});
