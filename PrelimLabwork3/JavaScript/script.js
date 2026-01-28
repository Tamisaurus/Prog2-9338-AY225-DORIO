function calculateGrade() {
    // 1. Get Inputs
    let attendance = parseFloat(document.getElementById('attendance').value);
    let lab1 = parseFloat(document.getElementById('lab1').value);
    let lab2 = parseFloat(document.getElementById('lab2').value);
    let lab3 = parseFloat(document.getElementById('lab3').value);

    // Validate if inputs are numbers
    if (isNaN(attendance) || isNaN(lab1) || isNaN(lab2) || isNaN(lab3)) {
        alert("Please enter valid numbers in all fields.");
        return;
    }

    // --- VALIDATION ADDED HERE ---
    // Check range 0-100
    if (attendance < 0 || attendance > 100 || 
        lab1 < 0 || lab1 > 100 || 
        lab2 < 0 || lab2 > 100 || 
        lab3 < 0 || lab3 > 100) {
        
        alert("All grades must be between 0 and 100.");
        return; // Stop the calculation
    }
    // -----------------------------

    // 2. Compute
    let labAvg = (lab1 + lab2 + lab3) / 3;
    let classStanding = (attendance * 0.40) + (labAvg * 0.60);

    // Formula: ReqExam = (Target - (ClassStanding * 0.70)) / 0.30
    let reqPass = (75 - (classStanding * 0.70)) / 0.30;
    let reqExc = (100 - (classStanding * 0.70)) / 0.30;

    // 3. Display Results
    document.getElementById('labAvg').innerText = labAvg.toFixed(2);
    document.getElementById('classStanding').innerText = classStanding.toFixed(2);

    // Update Pass Requirement
    let passEl = document.getElementById('reqPass');
    let passRem = document.getElementById('remarkPass');
    
    passEl.innerText = reqPass.toFixed(2);
    if (reqPass > 100) {
        passRem.innerText = "(Impossible)";
        passRem.style.color = "#c0392b"; 
    } else if (reqPass <= 0) {
        passEl.innerText = "0.00";
        passRem.innerText = "(Secured)";
        passRem.style.color = "green";
    } else {
        passRem.innerText = "";
    }

    // Update Excellent Requirement
    let excEl = document.getElementById('reqExc');
    let excRem = document.getElementById('remarkExc');

    excEl.innerText = reqExc.toFixed(2);
    if (reqExc > 100) {
        excRem.innerText = "(Impossible)";
        excRem.style.color = "#c0392b"; // Red
    } else if (reqExc <= 0) {
        excEl.innerText = "0.00";
        excRem.innerText = "(Secured)";
        excRem.style.color = "green";
    } else {
        excRem.innerText = "";
    }

    // Show result box
    document.getElementById('results').style.display = 'block';
}