const progressBar = document.getElementById("progress-bar");
const startOfSemester = new Date(Date.UTC(2024, 8, 15)).getTime();
const endOfSemester = new Date(Date.UTC(2024, 11, 20)).getTime();
const now = Date.now();
const total = endOfSemester - startOfSemester;
const current = now - startOfSemester;
const percentage = Math.min(100, Math.ceil((current / total) * 100));
progressBar.style.width = percentage + "%";