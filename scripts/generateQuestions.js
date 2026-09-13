// QuestionPool (Firestore) icin Gemini ile otomatik soru uretimi.
// Kullanim: GEMINI_API_KEY=... node generateQuestions.js [adet]
// Gereksinim: bu klasorde serviceAccountKey.json (bkz. scripts/README.md)
//
// Bu script docs/question-generation-prompt.md'deki ayni sema ve mufredati
// kullanir, Gemini'den yapilandirilmis JSON ister, sema + tekrar (duplicate)
// kontrolunden gecirir ve dogrudan QuestionPool'a yazar (onay kuyrugu yok).

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

// NOT: "Güncel Bilgiler" kasitli olarak otomatik uretimden HARIC tutuluyor.
// Gemini'nin (herhangi bir LLM'in) egitim kesim tarihi var; web aramasi
// (grounding) olmadan "guncel" gundem sorusu urettirmek eski/hatali bilgi
// riski tasir. Bu kategori icin manuel ekleme (admin panel) veya ileride
// grounding destekli ayri bir akis kullanilmali. Manuel yol ve
// uploadQuestions.js hala 6 kategorinin tamamini destekliyor, sadece bu
// otomatik script'ten cikarildi.
const VALID_CATEGORIES = ['Türkçe', 'Matematik', 'Tarih', 'Coğrafya', 'Vatandaşlık'];
const VALID_CORRECT = ['A', 'B', 'C', 'D'];
const REQUIRED_FIELDS = ['text', 'optionA', 'optionB', 'optionC', 'optionD', 'correct', 'category'];

const GEMINI_MODEL = process.env.GEMINI_MODEL || 'gemini-2.5-pro';
const GEMINI_API_KEY = process.env.GEMINI_API_KEY;
const serviceAccountPath = process.env.SERVICE_ACCOUNT_PATH || path.join(__dirname, 'serviceAccountKey.json');
const requestedCount = parseInt(process.argv[2] || process.env.QUESTION_COUNT || '40', 10);

// Gercek OSYM agirliklari (bkz. docs/question-generation-prompt.md).
// "Güncel Bilgiler" (%5) haric tutuldugu icin toplam 0.95 eder; bu sorun
// degil, countByCategoryForBatch kalan farki otomatik dagitir.
const CATEGORY_WEIGHTS = {
  'Türkçe': 0.25,
  'Matematik': 0.25,
  'Tarih': 0.225,
  'Coğrafya': 0.15,
  'Vatandaşlık': 0.075,
};

const CURRICULUM = `
- Türkçe: sözcükte anlam, cümlede anlam, paragraf, ses bilgisi/yazım/noktalama,
  sözcük yapısı ve türleri, fiilde çatı/fiilimsi, cümlenin öğeleri ve türleri,
  sözel mantık ve muhakeme (~%13'ü bu topic'te olsun)
- Matematik: temel kavramlar/bölünebilme/EBOB-EKOK, rasyonel sayılar/eşitsizlik/
  mutlak değer, üslü-köklü sayılar/çarpanlara ayırma, oran-orantı, problemler
  (sayı/kesir/yaş/işçi-havuz/hareket/yüzde/kâr-zarar/karışım), kümeler/
  fonksiyonlar/permütasyon-kombinasyon/olasılık, geometri, grafik-tablo
  yorumlama, sayısal mantık (~%20'si bu topic'te olsun)
- Tarih: İslamiyet öncesi Türk tarihi, Türk-İslam tarihi, Osmanlı tarihi,
  İnkılap tarihi (Kurtuluş Savaşı, Atatürk İlke ve İnkılapları), Çağdaş Türk
  ve Dünya tarihi
- Coğrafya: fiziki coğrafya, nüfus/yerleşme/göç, tarım/sanayi/ulaşım/turizm,
  bölgesel kalkınma projeleri, harita bilgisi, dünya coğrafyası/çevre-afetler
- Vatandaşlık: temel hukuk, 1982 Anayasası ve temel hak/özgürlükler,
  yasama-yürütme-yargı, idare hukuku/yerel yönetim, uluslararası kuruluşlar
- Güncel Bilgiler: Türkiye ve dünya gündemi
`.trim();

function buildPrompt(countByCategory) {
  const breakdown = Object.entries(countByCategory)
    .filter(([, n]) => n > 0)
    .map(([cat, n]) => `- ${cat}: ${n} soru`)
    .join('\n');
  return `
Sen bir KPSS (Kamu Personeli Seçme Sınavı) soru yazarısın. Aşağıdaki kurallara
göre gerçekçi, sınav standardında çoktan seçmeli sorular üret.

KURALLAR:
1. Her soru objesi TAM OLARAK şu alanlara sahip olacak (başka alan ekleme,
   isim değiştirme): "text", "optionA", "optionB", "optionC", "optionD",
   "correct" ("A"|"B"|"C"|"D"), "category" (aşağıdaki dağılımdaki değerlerden
   biri), "topic" (alt konu), "explanation" (doğru cevabın 1-3 cümlelik kısa
   açıklaması).
2. Sorular Türkçe, KPSS Lisans/Önlisans/Ortaöğretim Genel Yetenek-Genel Kültür
   seviyesinde, gerçek ÖSYM sınav formatına uygun olacak.
3. Şu kategori dağılımına TAM UYARAK üret:
${breakdown}
4. Aşağıdaki müfredattaki alt konulardan mümkün olduğunca dengeli dağıt:
${CURRICULUM}
5. Zorluk seviyesi kolay/orta/zor karışık olsun.
6. Aynı soruyu veya çok benzer varyasyonlarını tekrar ETME.
7. Her sorunun tek bir doğru cevabı olsun, şıklar birbirine yakın uzunlukta ve
   mantıklı çeldiriciler içersin (belli olmayan/saçma şıklar yazma).
`.trim();
}

const RESPONSE_SCHEMA = {
  type: 'ARRAY',
  items: {
    type: 'OBJECT',
    properties: {
      text: { type: 'STRING' },
      optionA: { type: 'STRING' },
      optionB: { type: 'STRING' },
      optionC: { type: 'STRING' },
      optionD: { type: 'STRING' },
      correct: { type: 'STRING', enum: VALID_CORRECT },
      category: { type: 'STRING', enum: VALID_CATEGORIES },
      topic: { type: 'STRING' },
      explanation: { type: 'STRING' },
    },
    required: REQUIRED_FIELDS.concat(['explanation']),
  },
};

// En buyuk kalan yontemi (largest remainder method): her kategoriye once
// tam kisim, sonra kalan farki en buyuk ondalik kalana sahip kategorilere
// tek tek dagitir. Negatif veya toplami "total"i asan/eksik kalan olmaz.
function countByCategoryForBatch(total) {
  const cats = Object.keys(CATEGORY_WEIGHTS);
  const exact = cats.map((cat) => total * CATEGORY_WEIGHTS[cat]);
  const floors = exact.map(Math.floor);
  let remaining = total - floors.reduce((a, b) => a + b, 0);
  const order = cats
    .map((cat, i) => ({ i, remainder: exact[i] - floors[i] }))
    .sort((a, b) => b.remainder - a.remainder);
  const counts = {};
  cats.forEach((cat, i) => { counts[cat] = floors[i]; });
  for (let k = 0; k < remaining; k++) {
    counts[cats[order[k % order.length].i]]++;
  }
  return counts;
}

async function callGemini(prompt) {
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent?key=${GEMINI_API_KEY}`;
  const body = {
    contents: [{ role: 'user', parts: [{ text: prompt }] }],
    generationConfig: {
      responseMimeType: 'application/json',
      responseSchema: RESPONSE_SCHEMA,
      temperature: 1.0,
    },
  };
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    const errText = await res.text();
    throw new Error(`Gemini API hatasi (${res.status}): ${errText.slice(0, 500)}`);
  }
  const data = await res.json();
  const text = data.candidates?.[0]?.content?.parts?.[0]?.text;
  if (!text) throw new Error('Gemini yanitinda beklenen icerik yok: ' + JSON.stringify(data).slice(0, 500));
  return JSON.parse(text);
}

function validate(q) {
  const missing = REQUIRED_FIELDS.filter((f) => !q[f] || typeof q[f] !== 'string' || !q[f].trim());
  if (missing.length) return `eksik alan: ${missing.join(', ')}`;
  if (!VALID_CORRECT.includes(q.correct)) return `gecersiz correct: ${q.correct}`;
  if (!VALID_CATEGORIES.includes(q.category)) return `gecersiz category: ${q.category}`;
  const opts = [q.optionA, q.optionB, q.optionC, q.optionD];
  if (new Set(opts.map(normalize)).size !== 4) return 'siklar birbirinin ayni veya bos';
  if (q.text.trim().length < 10) return 'soru metni cok kisa';
  return null;
}

function normalize(text) {
  // NFKD kasitli olarak kullanilmiyor: Turkce harfleri (ü, ö, ç, ş, ğ, ı, İ)
  // taban harf + birlesik aksan isaretine ayirir, bu da kelime icine
  // istenmeyen bosluklar sokar (\p{L} aksan isaretini harf saymaz).
  // \p{L}/\p{N} zaten on-birlesik Turkce harfleri doğru eşliyor.
  return text
    .toLocaleLowerCase('tr')
    .replace(/[^\p{L}\p{N}]+/gu, ' ')
    .trim()
    .replace(/\s+/g, ' ');
}

function questionHashId(q) {
  return crypto.createHash('sha1').update(normalize(q.text)).digest('hex');
}

async function main() {
  if (!GEMINI_API_KEY) {
    console.error('GEMINI_API_KEY ortam degiskeni gerekli.');
    process.exit(1);
  }
  if (!fs.existsSync(serviceAccountPath)) {
    console.error(`Servis hesabi anahtari bulunamadi: ${serviceAccountPath}`);
    process.exit(1);
  }
  if (!Number.isFinite(requestedCount) || requestedCount <= 0) {
    console.error('Gecersiz soru adedi.');
    process.exit(1);
  }

  admin.initializeApp({ credential: admin.credential.cert(require(serviceAccountPath)) });
  const db = admin.firestore();

  console.log(`Mevcut havuz okunuyor (tekrar kontrolu icin)...`);
  const existingSnap = await db.collection('QuestionPool').select('text').get();
  const existingNormalized = new Set(existingSnap.docs.map((d) => normalize(d.data().text || '')));
  console.log(`Havuzda ${existingSnap.size} soru var.`);

  const countByCategory = countByCategoryForBatch(requestedCount);
  console.log(`Istenen dagilim: ${JSON.stringify(countByCategory)}`);

  const prompt = buildPrompt(countByCategory);
  console.log(`Gemini (${GEMINI_MODEL}) cagriliyor, ${requestedCount} soru isteniyor...`);
  const generated = await callGemini(prompt);
  if (!Array.isArray(generated)) throw new Error('Gemini yaniti bir dizi (array) degil.');
  console.log(`Gemini ${generated.length} soru dondurdu, dogrulaniyor...`);

  let invalidCount = 0;
  let duplicateCount = 0;
  const toWrite = [];
  const seenInBatch = new Set();

  for (const q of generated) {
    const error = validate(q);
    if (error) {
      console.warn(`Atlandi (${error}): ${(q.text || '').slice(0, 60)}`);
      invalidCount++;
      continue;
    }
    const norm = normalize(q.text);
    if (existingNormalized.has(norm) || seenInBatch.has(norm)) {
      console.warn(`Atlandi (tekrar/duplicate): ${q.text.slice(0, 60)}`);
      duplicateCount++;
      continue;
    }
    seenInBatch.add(norm);
    toWrite.push(q);
  }

  console.log(`Firestore'a yaziliyor: ${toWrite.length} soru (${invalidCount} gecersiz, ${duplicateCount} tekrar elendi)...`);
  const BATCH_SIZE = 400;
  for (let i = 0; i < toWrite.length; i += BATCH_SIZE) {
    const chunk = toWrite.slice(i, i + BATCH_SIZE);
    const batch = db.batch();
    for (const q of chunk) {
      const id = questionHashId(q);
      batch.set(db.collection('QuestionPool').doc(id), {
        id,
        text: q.text,
        optionA: q.optionA,
        optionB: q.optionB,
        optionC: q.optionC,
        optionD: q.optionD,
        correct: q.correct,
        category: q.category,
        explanation: q.explanation || '',
        ...(q.topic ? { topic: q.topic } : {}),
        source: 'ai-auto',
        generatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
    await batch.commit();
  }

  const finalByCategory = {};
  toWrite.forEach((q) => { finalByCategory[q.category] = (finalByCategory[q.category] || 0) + 1; });
  console.log(`\nTamamlandi: ${toWrite.length} yeni soru eklendi.`);
  console.log('Kategori dagilimi (bu calistirmada eklenen):', finalByCategory);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
