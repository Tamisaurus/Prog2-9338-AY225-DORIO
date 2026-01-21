// --- INITIALIZATION ---
if (!localStorage.getItem('userDatabase')) {
    const defaultDB = [{ fullname: "Mr.Pogi", username: "pogiako", password: "pogiako" }, { fullname: "Ms.Maganda", username: "magandaako", password: "magandaako" }];
    localStorage.setItem('userDatabase', JSON.stringify(defaultDB));
}

let currentUserFullname = "";

// --- NAVIGATION ---
function showRegister() {
    document.getElementById('login-screen').classList.add('hidden');
    document.getElementById('register-screen').classList.remove('hidden');
    clearInputs();
}

function showLogin() {
    document.getElementById('register-screen').classList.add('hidden');
    document.getElementById('login-screen').classList.remove('hidden');
}

function logout() {
    document.getElementById('dashboard-screen').classList.add('hidden');
    document.getElementById('login-screen').classList.remove('hidden');
    clearInputs();
}

function clearInputs() {
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => input.value = '');
    document.getElementById('rbIn').checked = true;
}

// --- LOGIC: REGISTER ---
function handleRegister() {
    const fullname = document.getElementById('reg-fullname').value.trim();
    const user = document.getElementById('reg-user').value.trim();
    const pass = document.getElementById('reg-pass').value.trim();

    if (!fullname || !user || !pass) { alert("Fill all fields"); return; }

    let db = JSON.parse(localStorage.getItem('userDatabase'));
    const exists = db.some(u => u.username.toLowerCase() === user.toLowerCase());
    if (exists) { alert("Error: Username already taken."); return; }

    db.push({ fullname, username: user, password: pass });
    localStorage.setItem('userDatabase', JSON.stringify(db));

    downloadUserDatabase();
    alert("Registered! Please Login.");
    showLogin();
}

// --- LOGIC: LOGIN ---
function handleLogin() {
    const user = document.getElementById('login-user').value.trim();
    const pass = document.getElementById('login-pass').value.trim();
    
    let db = JSON.parse(localStorage.getItem('userDatabase'));
    const account = db.find(u => u.username === user && u.password === pass);

    if (account) {
        currentUserFullname = account.fullname;
        document.getElementById('display-fullname').innerText = currentUserFullname;
        document.getElementById('login-screen').classList.add('hidden');
        document.getElementById('dashboard-screen').classList.remove('hidden');
        refreshTableDisplay(); 
    } else {
        playBeep();
        alert("Invalid Credentials");
    }
}

// --- LOGIC: ATTENDANCE ---
function getTimestamp() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

function generateID() {
    return Math.random().toString(36).substring(2, 10).toUpperCase();
}

function recordAttendance() {
    const name = document.getElementById('nameField').value.trim();
    const course = document.getElementById('courseField').value.trim();
    const year = document.getElementById('yearField').value.trim();
    const isTimeIn = document.getElementById('rbIn').checked;
    const type = isTimeIn ? "TIME-IN" : "TIME-OUT";

    if (!name || !course || !year) { alert("Fill all fields"); return; }

    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
    const studentRecords = history.filter(rec => rec.name.toLowerCase() === name.toLowerCase());
    const lastRecord = studentRecords[studentRecords.length - 1];

    if (isTimeIn) {
        if (lastRecord && lastRecord.type === "TIME-IN") {
            playBeep();
            alert(`ERROR: ${name} is ALREADY Timed In.\nYou must Time Out first.`);
            return;
        }
    } else {
        if (!lastRecord || lastRecord.type === "TIME-OUT") {
            playBeep();
            alert(`ERROR: ${name} is NOT Timed In.\nYou must Time In first.`);
            return;
        }
    }

    const timestamp = getTimestamp();
    const idSig = generateID();

    document.getElementById('timeField').value = timestamp;
    document.getElementById('sigField').value = idSig;

    const newRecord = { type, name, course, year, time: timestamp, id: idSig };
    history.push(newRecord);
    localStorage.setItem('attendanceLogs', JSON.stringify(history));

    alert(`Successfully Recorded: ${type}`);
    refreshTableDisplay();
}

function refreshTableDisplay() {
    const tbodyIn = document.querySelector('#tableIn tbody');
    const tbodyOut = document.querySelector('#tableOut tbody');
    tbodyIn.innerHTML = "";
    tbodyOut.innerHTML = "";

    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];

    history.forEach(rec => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${rec.name}</td>
            <td>${rec.course}</td>
            <td>${rec.year}</td>
            <td style="color: ${rec.type === 'TIME-IN' ? '#4CAF50' : '#ff6464'}; font-family: monospace;">${rec.time}</td>
        `;

        if (rec.type === "TIME-IN") tbodyIn.appendChild(row);
        else tbodyOut.appendChild(row);
    });
}

function clearAttendanceLogs() {
    if (confirm("Are you sure you want to delete ALL attendance records?")) {
        localStorage.setItem('attendanceLogs', '[]'); 
        refreshTableDisplay(); 
        
        document.getElementById('nameField').value = "";
        document.getElementById('courseField').value = "";
        document.getElementById('yearField').value = "";
        document.getElementById('timeField').value = "";
        document.getElementById('sigField').value = "";
        
        alert("Attendance logs cleared.");
    }
}

// --- UTILITIES ---
function playBeep() {
    const audio = new Audio('beep.mp3'); 
    audio.play().catch(e => console.log("Audio needed interaction"));
}

function downloadAttendanceLogs() {
    const history = JSON.parse(localStorage.getItem('attendanceLogs')) || [];
    
    let content = "ATTENDANCE RECORDS LOG\n======================\n\n";
    content += "TYPE      | NAME                | COURSE   | YEAR | TIME                 | ID\n";
    content += "--------------------------------------------------------------------------------------\n";

    history.forEach(rec => {
        const type = rec.type.padEnd(9, ' ');
        const name = rec.name.padEnd(19, ' ');
        const course = rec.course.padEnd(8, ' ');
        const year = rec.year.padEnd(4, ' ');
        content += `${type} | ${name} | ${course} | ${year} | ${rec.time} | ${rec.id}\n`;
    });

    downloadFile("Attendance_Logs.txt", content);
}

function downloadUserDatabase() {
    let db = JSON.parse(localStorage.getItem('userDatabase'));
    let content = "USER DATABASE RECORD\n====================\n\n";
    db.forEach(u => {
        content += `User: ${u.username} | Pass: ${u.password} | Name: ${u.fullname}\n`;
    });
    downloadFile("database.txt", content);
}

function downloadFile(filename, content) {
    const blob = new Blob([content], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = filename;
    link.click();
}

document.getElementById('login-pass').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') handleLogin();
});