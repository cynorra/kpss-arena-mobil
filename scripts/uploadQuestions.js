// QuestionPool (Firestore) icin toplu soru yukleme scripti.
// Kullanim: node uploadQuestions.js <sorular.json>
// Gereksinim: bu klasorde serviceAccountKey.json (bkz. scripts/README.md)

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const VALID_CATEGORIES = ['Türkçe', 'Matematik', 'Tarih', 'Coğrafya', 'Vatandaşlık', 'Güncel Bilgiler'];
const VALID_CORRECT = ['A', 'B', 'C', 'D'];
const REQUIRED_FIELDS = ['text', 'optionA', 'optionB', 'optionC', 'optionD', 'correct', 'category'];

const serviceAccountPath = process.env.SERVICE_ACCOUNT_PATH || path.join(__dirname, 'serviceAccountKey.json');
const questionsPath = process.argv[2];

if (!questionsPath) {
  console.error('Kullanim: node uploadQuestions.js <sorular.json>');
  process.exit(1);
}
if (!fs.existsSync(serviceAccountPath)) {
  console.error(`Servis hesabi anahtari bulunamadi: ${serviceAccountPath}`);
  console.error('Firebase Console > Proje Ayarlari > Servis Hesaplari > Yeni Ozel Anahtar Olustur');
  process.exit(1);
}
if (!fs.existsSync(questionsPath)) {
  console.error(`Soru dosyasi bulunamadi: ${questionsPath}`);
  process.exit(1);
}

admin.initializeApp({ credential: admin.credential.cert(require(serviceAccountPath)) });
const db = admin.firestore();

function validate(q) {
  const missing = REQUIRED_FIELDS.filter((f) => !q[f]);
  if (missing.length) return `eksik alan: ${missing.join(', ')}`;
  if (!VALID_CORRECT.includes(q.correct)) return `gecersiz correct: ${q.correct}`;
  if (!VALID_CATEGORIES.includes(q.category)) return `gecersiz category: ${q.category}`;
  return null;
}

async function main() {
  const questions = JSON.parse(fs.readFileSync(questionsPath, 'utf8'));
  if (!Array.isArray(questions)) throw new Error('JSON dosyasi bir dizi (array) olmali.');

  let ok = 0;
  let skipped = 0;
  const BATCH_SIZE = 400;

  for (let i = 0; i < questions.length; i += BATCH_SIZE) {
    const chunk = questions.slice(i, i + BATCH_SIZE);
    const batch = db.batch();

    for (const q of chunk) {
      const error = validate(q);
      if (error) {
        console.warn(`Atlandi (${error}): ${(q.text || '').slice(0, 50)}`);
        skipped++;
        continue;
      }
      const id = q.id || crypto.randomUUID();
      const docRef = db.collection('QuestionPool').doc(id);
      batch.set(docRef, {
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
      });
      ok++;
    }

    await batch.commit();
  }

  console.log(`Tamamlandi: ${ok} soru eklendi, ${skipped} soru atlandi.`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
