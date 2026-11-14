// Sends a push notification to every device subscribed to the "all" topic (the app subscribes on launch).
//
//   node sendPush.js "Başlık" "Mesaj metni"            -> dry run (validates only, nothing is delivered)
//   node sendPush.js "Başlık" "Mesaj metni" --send     -> really sends
//
// Uses scripts/serviceAccountKey.json (never committed). Keep sends rare (new test, events):
// the in-app reminders already cover the daily nudges.
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

const [title, body] = process.argv.slice(2).filter((a) => !a.startsWith('--'));
const reallySend = process.argv.includes('--send');

if (!title || !body) {
  console.error('Usage: node sendPush.js "<title>" "<body>" [--send]');
  process.exit(1);
}

admin.initializeApp({ credential: admin.credential.cert(serviceAccount) });

admin
  .messaging()
  .send({ topic: 'all', notification: { title, body } }, !reallySend)
  .then((id) => {
    console.log(reallySend ? `Sent: ${id}` : `Dry run OK (not delivered): ${id}`);
    process.exit(0);
  })
  .catch((e) => {
    console.error('Failed:', e.message);
    process.exit(1);
  });
