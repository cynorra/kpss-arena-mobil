// sorular3 uretici: node docs/build_sorular3.js -> docs/sorular3.json (veri: sorular3_data.js)
const fs = require('fs');const path = require('path');const crypto = require('crypto');
const { TURKCE, MATEMATIK, TARIH, COGRAFYA, VATANDASLIK } = require('./sorular3_data.js'); const GUNCEL = [];
const SETS = [['Türkçe', TURKCE], ['Matematik', MATEMATIK], ['Tarih', TARIH], ['Coğrafya', COGRAFYA], ['Vatandaşlık', VATANDASLIK], ['Güncel Bilgiler', GUNCEL]];
const LETTERS = ['A', 'B', 'C', 'D'];

// ---- deterministik sozde-rastgele (her calistirmada ayni sonuc)
let seed = 20261114;
function rnd() { seed = (seed * 1664525 + 1013904223) % 4294967296; return seed / 4294967296; }
function uuid() {
  const b = crypto.randomBytes(16);
  b[6] = (b[6] & 0x0f) | 0x40; b[8] = (b[8] & 0x3f) | 0x80;
  const h = b.toString('hex');
  return `${h.slice(0, 8)}-${h.slice(8, 12)}-${h.slice(12, 16)}-${h.slice(16, 20)}-${h.slice(20)}`;
}

// ---- onceki calistirmadan id'leri koru (ayni metin -> ayni id)
const outPath = path.join(__dirname, 'sorular3.json');
const prevIds = {};
if (fs.existsSync(outPath)) {
  for (const q of JSON.parse(fs.readFileSync(outPath, 'utf8'))) prevIds[q.text] = q.id;
}

const out = [];
const errors = [];
for (const [category, items] of SETS) {
  for (const it of items) {
    const [text, opts, correctIdx, topic, explanation, ordered] = it;
    if (opts.length !== 4) errors.push(`4 sik degil: ${text.slice(0, 40)}`);
    if (new Set(opts).size !== 4) errors.push(`tekrarlayan sik: ${text.slice(0, 40)}`);
    if (correctIdx < 0 || correctIdx > 3) errors.push(`gecersiz dogru index: ${text.slice(0, 40)}`);
    out.push({ category, text, opts: opts.slice(), correct: opts[correctIdx], topic, explanation, ordered: !!ordered });
  }
}
if (errors.length) { console.error(errors.join('\n')); process.exit(1); }

// ---- siklari dagit: siralilar artan/azalan, digerleri karisik; dogru sik dagilimini dengele
const targetPer = Math.ceil(out.length / 4);
const counts = { A: 0, B: 0, C: 0, D: 0 };
for (const q of out) {
  let arrangements = [];
  if (q.ordered) {
    arrangements = [q.opts.slice(), q.opts.slice().reverse()];
  } else {
    for (let t = 0; t < 12; t++) {
      const a = q.opts.slice();
      for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(rnd() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; }
      arrangements.push(a);
    }
  }
  // dogru siki en az dolu harfe koyan duzeni sec
  arrangements.sort((x, y) => counts[LETTERS[x.indexOf(q.correct)]] - counts[LETTERS[y.indexOf(q.correct)]]);
  const best = arrangements[0];
  q.opts = best;
  q.letter = LETTERS[best.indexOf(q.correct)];
  counts[q.letter]++;
}

const final = out.map((q) => ({
  id: prevIds[q.text] || uuid(),
  text: q.text,
  optionA: q.opts[0], optionB: q.opts[1], optionC: q.opts[2], optionD: q.opts[3],
  correct: q.letter,
  category: q.category,
  topic: q.topic,
  explanation: q.explanation,
}));

fs.writeFileSync(outPath, JSON.stringify(final, null, 2) + '\n', 'utf8');
const byCat = {};
final.forEach((q) => { byCat[q.category] = (byCat[q.category] || 0) + 1; });
console.log('Toplam:', final.length, byCat);
console.log('Dogru sik dagilimi:', counts);
