const admin = require('firebase-admin');
const path = require('path');

admin.initializeApp({ credential: admin.credential.cert(require(path.join(__dirname, 'serviceAccountKey.json'))) });
const db = admin.firestore();

async function main() {
  const snap = await db.collection('adminUsers').get();
  console.log(`adminUsers dokuman sayisi: ${snap.size}`);
  snap.forEach((doc) => console.log(`  - ${doc.id}:`, JSON.stringify(doc.data())));

  const dailyTests = await db.collection('dailyTests').get();
  console.log(`\ndailyTests dokuman sayisi: ${dailyTests.size}`);
  dailyTests.forEach((doc) => console.log(`  - ${doc.id}`));
}

main().catch((e) => { console.error(e); process.exit(1); });
