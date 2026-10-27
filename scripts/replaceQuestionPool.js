// QuestionPool (Firestore) koleksiyonunun icerigini bir JSON dosyasiyla DEGISTIRIR.
//
//   node replaceQuestionPool.js ../docs/sorular_final.json            -> PROVA: ne yapacagini yazar, hicbir sey degistirmez
//   node replaceQuestionPool.js ../docs/sorular_final.json --apply    -> uygular
//
// Guvenlik:
//  * --apply olmadan hicbir yazma/silme yapilmaz.
//  * Uygulamadan once mevcut havuz scripts/backups/ altina tam yedeklenir.
//  * JSON'daki her sorunun "id" alani zorunludur; ayni dosyayla tekrar calistirmak guvenlidir (ayni id uzerine yazar).
//  * Dosyada olmayan id'li eski dokumanlar silinir (hatali eski sorulari temizlemek icin).

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

const VALID_CATEGORIES = ['Türkçe', 'Matematik', 'Tarih', 'Coğrafya', 'Vatandaşlık', 'Güncel Bilgiler'];
const VALID_CORRECT = ['A', 'B', 'C', 'D'];
const REQUIRED = ['id', 'text', 'optionA', 'optionB', 'optionC', 'optionD', 'correct', 'category'];

const questionsPath = process.argv[2];
const apply = process.argv.includes('--apply');
const serviceAccountPath = process.env.SERVICE_ACCOUNT_PATH || path.join(__dirname, 'serviceAccountKey.json');

if (!questionsPath || !fs.existsSync(questionsPath)) {
  console.error('Kullanim: node replaceQuestionPool.js <sorular.json> [--apply]');
  process.exit(1);
}
if (!fs.existsSync(serviceAccountPath)) {
  console.error(`Servis hesabi anahtari bulunamadi: ${serviceAccountPath}`);
  process.exit(1);
}

const questions = JSON.parse(fs.readFileSync(questionsPath, 'utf8'));
if (!Array.isArray(questions) || questions.length === 0) throw new Error('JSON dosyasi bos olmayan bir dizi olmali.');

const ids = new Set();
for (const q of questions) {
  const missing = REQUIRED.filter((f) => !q[f]);
  if (missing.length) throw new Error(`Eksik alan (${missing.join(', ')}): ${(q.text || '').slice(0, 50)}`);
  if (!VALID_CORRECT.includes(q.correct)) throw new Error(`Gecersiz correct: ${q.text.slice(0, 50)}`);
  if (!VALID_CATEGORIES.includes(q.category)) throw new Error(`Gecersiz category: ${q.text.slice(0, 50)}`);
  if (ids.has(q.id)) throw new Error(`Tekrarlayan id: ${q.id}`);
  ids.add(q.id);
}

admin.initializeApp({ credential: admin.credential.cert(require(serviceAccountPath)) });
const db = admin.firestore();

async function main() {
  const existing = await db.collection('QuestionPool').get();
  const toDelete = existing.docs.filter((d) => !ids.has(d.id));
  const byCategory = {};
  questions.forEach((q) => { byCategory[q.category] = (byCategory[q.category] || 0) + 1; });

  console.log(`Mevcut havuz   : ${existing.size} dokuman`);
  console.log(`Yuklenecek     : ${questions.length} soru`, byCategory);
  console.log(`Silinecek eski : ${toDelete.length} dokuman (yeni dosyada olmayan id'ler)`);

  if (!apply) {
    console.log('\nPROVA modu: hicbir sey degistirilmedi. Uygulamak icin komuta --apply ekleyin.');
    return;
  }

  // 1) yedek
  const backupDir = path.join(__dirname, 'backups');
  fs.mkdirSync(backupDir, { recursive: true });
  const stamp = new Date().toISOString().replace(/[:.]/g, '-');
  const backupFile = path.join(backupDir, `questionpool-${stamp}.json`);
  fs.writeFileSync(backupFile, JSON.stringify(existing.docs.map((d) => ({ _docId: d.id, ...d.data() })), null, 2), 'utf8');
  console.log(`Yedek yazildi  : ${backupFile}`);

  // 2) yeni sorulari yaz
  const BATCH = 400;
  for (let i = 0; i < questions.length; i += BATCH) {
    const batch = db.batch();
    for (const q of questions.slice(i, i + BATCH)) {
      batch.set(db.collection('QuestionPool').doc(q.id), {
        id: q.id,
        text: q.text,
        optionA: q.optionA,
        optionB: q.optionB,
        optionC: q.optionC,
        optionD: q.optionD,
        correct: q.correct,
        category: q.category,
        topic: q.topic || '',
        explanation: q.explanation || '',
      });
    }
    await batch.commit();
  }
  console.log(`Yazildi        : ${questions.length} soru`);

  // 3) yeni dosyada olmayan eski dokumanlari sil
  for (let i = 0; i < toDelete.length; i += BATCH) {
    const batch = db.batch();
    toDelete.slice(i, i + BATCH).forEach((d) => batch.delete(d.ref));
    await batch.commit();
  }
  console.log(`Silindi        : ${toDelete.length} eski dokuman`);

  const after = await db.collection('QuestionPool').count().get();
  console.log(`Son durum      : ${after.data().count} dokuman`);
}

main().then(() => process.exit(0)).catch((e) => { console.error(e); process.exit(1); });
