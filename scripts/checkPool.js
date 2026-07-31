// QuestionPool koleksiyonunu denetler: sayim, kategori dagilimi, eksik/gecersiz alan taramasi.
const admin = require('firebase-admin');
const path = require('path');

const serviceAccountPath = path.join(__dirname, 'serviceAccountKey.json');
admin.initializeApp({ credential: admin.credential.cert(require(serviceAccountPath)) });
const db = admin.firestore();

const VALID_CATEGORIES = ['Türkçe', 'Matematik', 'Tarih', 'Coğrafya', 'Vatandaşlık', 'Güncel Bilgiler'];
const VALID_CORRECT = ['A', 'B', 'C', 'D'];
const REQUIRED_FIELDS = ['id', 'text', 'optionA', 'optionB', 'optionC', 'optionD', 'correct', 'category'];

async function main() {
  const snap = await db.collection('QuestionPool').get();
  console.log(`Toplam doküman: ${snap.size}`);

  const byCategory = {};
  const idMismatches = [];
  const invalid = [];
  const seenIds = new Set();
  const dupIds = [];

  snap.forEach((doc) => {
    const q = doc.data();
    byCategory[q.category || '(yok)'] = (byCategory[q.category || '(yok)'] || 0) + 1;

    const missing = REQUIRED_FIELDS.filter((f) => !q[f]);
    if (missing.length) invalid.push({ docId: doc.id, reason: `eksik: ${missing.join(', ')}` });
    else {
      if (!VALID_CORRECT.includes(q.correct)) invalid.push({ docId: doc.id, reason: `gecersiz correct: ${q.correct}` });
      if (!VALID_CATEGORIES.includes(q.category)) invalid.push({ docId: doc.id, reason: `gecersiz category: ${q.category}` });
    }
    if (q.id && q.id !== doc.id) idMismatches.push({ docId: doc.id, fieldId: q.id });
    if (seenIds.has(doc.id)) dupIds.push(doc.id);
    seenIds.add(doc.id);
  });

  console.log('\nKategori dağılımı:');
  for (const [cat, count] of Object.entries(byCategory)) console.log(`  ${cat}: ${count}`);

  console.log(`\nGeçersiz/eksik alanlı doküman: ${invalid.length}`);
  invalid.slice(0, 20).forEach((i) => console.log(`  - ${i.docId}: ${i.reason}`));
  if (invalid.length > 20) console.log(`  ... ve ${invalid.length - 20} tane daha`);

  console.log(`\nid alanı != doküman ID olan: ${idMismatches.length}`);
  idMismatches.slice(0, 10).forEach((i) => console.log(`  - doc ${i.docId} -> id alanı ${i.fieldId}`));

  console.log(`\nYinelenen doküman ID: ${dupIds.length}`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
