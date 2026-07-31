const admin = require('firebase-admin');
const path = require('path');

admin.initializeApp({ credential: admin.credential.cert(require(path.join(__dirname, 'serviceAccountKey.json'))) });

async function main() {
  try {
    const list = await admin.auth().listUsers(50);
    console.log(`Auth kullanıcı sayısı: ${list.users.length}`);
    list.users.forEach((u) => console.log(`  - uid=${u.uid} email=${u.email || '(yok)'} displayName=${u.displayName || '(yok)'}`));
  } catch (e) {
    console.error('Auth listUsers hatasi:', e.code || e.message);
  }

  try {
    const snap = await admin.firestore().collection('users').get();
    console.log(`\nFirestore users doküman sayısı: ${snap.size}`);
    snap.forEach((doc) => console.log(`  - ${doc.id}:`, JSON.stringify(doc.data())));
  } catch (e) {
    console.error('Firestore users hatasi:', e.code || e.message);
  }
}

main();
