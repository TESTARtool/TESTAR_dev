var $state = {
  
  yearsInJob: 0,
  
  approvalMessage: '',
  
  interestRate: 0,
  
  employed: false,
  
  totalInterest: 0,
  
  hasCoSigner: false,
  
  not_approvalMessage: '',
  
  jobTitle: '',
  
  loanTerm: 0,
  
  age: 0,
  
  hasLicense: false,
  
  loanApproved: false,
  
  loanAmount: 0,
  
  healthcareExpenses: 0,
  
  monthlyExpenses: 0,
  
  income: 0,
  
  retired: false,
  
  annualPension: 0,
  
  totalRepayment: 0,
  
  netPension: 0,
  
  seniorDiscount: false,
  
  monthlyDebts: 0,
  
  yearsRetired: 0,
  
  monthlySalary: 0,
  
  lookingForJob: false,
  
  grade: 0,
  
  inSchool: false,
  
  monthlyPayment: 0,
  
  fullName: '',
  
  annualSavings: 0,
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('fullName_widget_25');
     
     elt.value = $state.fullName;
     
     div = document.getElementById('fullName_div_25');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('age_widget_70');
     
     elt.value = $state.age;
     
     div = document.getElementById('age_div_70');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('hasLicense_widget_104');
     
     elt.checked = $state.hasLicense;
     
     div = document.getElementById('hasLicense_div_104');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('inSchool_widget_183');
     
     elt.checked = $state.inSchool;
     
     div = document.getElementById('inSchool_div_183');
     div.style.display = (true && ($state.age < 18)) ? 'block' : 'none'; 
   
     elt = document.getElementById('grade_widget_258');
     
     elt.value = $state.grade;
     
     div = document.getElementById('grade_div_258');
     div.style.display = ((true && ($state.age < 18)) && $state.inSchool) ? 'block' : 'none'; 
   
     elt = document.getElementById('employed_widget_360');
     
     elt.checked = $state.employed;
     
     div = document.getElementById('employed_div_360');
     div.style.display = ((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) ? 'block' : 'none'; 
   
     elt = document.getElementById('jobTitle_widget_445');
     
     elt.value = $state.jobTitle;
     
     div = document.getElementById('jobTitle_div_445');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
   
     elt = document.getElementById('yearsInJob_widget_496');
     
     elt.value = $state.yearsInJob;
     
     div = document.getElementById('yearsInJob_div_496');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
   
     elt = document.getElementById('monthlySalary_widget_578');
     
     elt.value = $state.monthlySalary;
     
     div = document.getElementById('monthlySalary_div_578');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
   
     elt = document.getElementById('monthlyExpenses_widget_640');
     
     elt.value = $state.monthlyExpenses;
     
     div = document.getElementById('monthlyExpenses_div_640');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
   
     elt = document.getElementById('annualSavings_widget_713');
     
     elt.value = $state.annualSavings;
     
     div = document.getElementById('annualSavings_div_713');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
   
     elt = document.getElementById('lookingForJob_widget_822');
     
     elt.checked = $state.lookingForJob;
     
     div = document.getElementById('lookingForJob_div_822');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && !(($state.employed))) ? 'block' : 'none'; 
   
     elt = document.getElementById('retired_widget_910');
     
     elt.checked = $state.retired;
     
     div = document.getElementById('retired_div_910');
     div.style.display = ((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) ? 'block' : 'none'; 
   
     elt = document.getElementById('yearsRetired_widget_975');
     
     elt.value = $state.yearsRetired;
     
     div = document.getElementById('yearsRetired_div_975');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
   
     elt = document.getElementById('annualPension_widget_1039');
     
     elt.value = $state.annualPension;
     
     div = document.getElementById('annualPension_div_1039');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
   
     elt = document.getElementById('healthcareExpenses_widget_1101');
     
     elt.value = $state.healthcareExpenses;
     
     div = document.getElementById('healthcareExpenses_div_1101');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
   
     elt = document.getElementById('netPension_widget_1180');
     
     elt.value = $state.netPension;
     
     div = document.getElementById('netPension_div_1180');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
   
     elt = document.getElementById('seniorDiscount_widget_1296');
     
     elt.checked = $state.seniorDiscount;
     
     div = document.getElementById('seniorDiscount_div_1296');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('income_widget_1398');
     
     elt.value = $state.income;
     
     div = document.getElementById('income_div_1398');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('monthlyDebts_widget_1446');
     
     elt.value = $state.monthlyDebts;
     
     div = document.getElementById('monthlyDebts_div_1446');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('hasCoSigner_widget_1501');
     
     elt.checked = $state.hasCoSigner;
     
     div = document.getElementById('hasCoSigner_div_1501');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('loanAmount_widget_1564');
     
     elt.value = $state.loanAmount;
     
     div = document.getElementById('loanAmount_div_1564');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('interestRate_widget_1608');
     
     elt.value = $state.interestRate;
     
     div = document.getElementById('interestRate_div_1608');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('loanTerm_widget_1666');
     
     elt.value = $state.loanTerm;
     
     div = document.getElementById('loanTerm_div_1666');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('totalInterest_widget_1709');
     
     elt.value = $state.totalInterest;
     
     div = document.getElementById('totalInterest_div_1709');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('totalRepayment_widget_1809');
     
     elt.value = $state.totalRepayment;
     
     div = document.getElementById('totalRepayment_div_1809');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('monthlyPayment_widget_1892');
     
     elt.value = $state.monthlyPayment;
     
     div = document.getElementById('monthlyPayment_div_1892');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('loanApproved_widget_1984');
     
     elt.checked = $state.loanApproved;
     
     div = document.getElementById('loanApproved_div_1984');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('approvalMessage_widget_2114');
     
     elt.value = $state.approvalMessage;
     
     div = document.getElementById('approvalMessage_div_2114');
     div.style.display = (true && $state.loanApproved) ? 'block' : 'none'; 
   
     elt = document.getElementById('not_approvalMessage_widget_2214');
     
     elt.value = $state.not_approvalMessage;
     
     div = document.getElementById('not_approvalMessage_div_2214');
     div.style.display = (true && !(($state.loanApproved))) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('fullName_div_25');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('age_div_70');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('hasLicense_div_104');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('inSchool_div_183');
     div.style.display = (true && ($state.age < 18)) ? 'block' : 'none'; 
     
     
     div = document.getElementById('grade_div_258');
     div.style.display = ((true && ($state.age < 18)) && $state.inSchool) ? 'block' : 'none'; 
     
     
     div = document.getElementById('employed_div_360');
     div.style.display = ((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('jobTitle_div_445');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
     
     
     div = document.getElementById('yearsInJob_div_496');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
     
     
     div = document.getElementById('monthlySalary_div_578');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
     
     
     div = document.getElementById('monthlyExpenses_div_640');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
     
     
     div = document.getElementById('annualSavings_div_713');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed) ? 'block' : 'none'; 
     
     if ((((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && $state.employed)) {
        if (change === 'annualSavings') {
           console.log('ERROR: mutual exclusion bug on annualSavings');
           break;
        }
        newVal = ((($state.monthlySalary - $state.monthlyExpenses)) * 12);
        if (newVal !== $state.annualSavings) {
          let elt = document.getElementById('annualSavings_widget_713');
          $state.annualSavings = newVal;
          
          elt.value = newVal;
          
         change = 'annualSavings';
       }
     }
     
     
     div = document.getElementById('lookingForJob_div_822');
     div.style.display = (((true && !((($state.age < 18)))) && (($state.age >= 18) && ($state.age <= 65))) && !(($state.employed))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('retired_div_910');
     div.style.display = ((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('yearsRetired_div_975');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
     
     
     div = document.getElementById('annualPension_div_1039');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
     
     
     div = document.getElementById('healthcareExpenses_div_1101');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
     
     
     div = document.getElementById('netPension_div_1180');
     div.style.display = (((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired) ? 'block' : 'none'; 
     
     if ((((true && !((($state.age < 18)))) && !(((($state.age >= 18) && ($state.age <= 65))))) && $state.retired)) {
        if (change === 'netPension') {
           console.log('ERROR: mutual exclusion bug on netPension');
           break;
        }
        newVal = ($state.annualPension - $state.healthcareExpenses);
        if (newVal !== $state.netPension) {
          let elt = document.getElementById('netPension_widget_1180');
          $state.netPension = newVal;
          
          elt.value = newVal;
          
         change = 'netPension';
       }
     }
     
     
     div = document.getElementById('seniorDiscount_div_1296');
     div.style.display = true ? 'block' : 'none'; 
     
     if (true) {
        if (change === 'seniorDiscount') {
           console.log('ERROR: mutual exclusion bug on seniorDiscount');
           break;
        }
        newVal = (($state.age >= 65) && $state.retired);
        if (newVal !== $state.seniorDiscount) {
          let elt = document.getElementById('seniorDiscount_widget_1296');
          $state.seniorDiscount = newVal;
          
          elt.checked = newVal;
          
         change = 'seniorDiscount';
       }
     }
     
     
     div = document.getElementById('income_div_1398');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('monthlyDebts_div_1446');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('hasCoSigner_div_1501');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('loanAmount_div_1564');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('interestRate_div_1608');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('loanTerm_div_1666');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('totalInterest_div_1709');
     div.style.display = true ? 'block' : 'none'; 
     
     if (true) {
        if (change === 'totalInterest') {
           console.log('ERROR: mutual exclusion bug on totalInterest');
           break;
        }
        newVal = (function () { const div = 100; return div !== 0 ? Math.ceil(((($state.loanAmount * $state.interestRate) * $state.loanTerm)) / div) : 0; })();
        if (newVal !== $state.totalInterest) {
          let elt = document.getElementById('totalInterest_widget_1709');
          $state.totalInterest = newVal;
          
          elt.value = newVal;
          
         change = 'totalInterest';
       }
     }
     
     
     div = document.getElementById('totalRepayment_div_1809');
     div.style.display = true ? 'block' : 'none'; 
     
     if (true) {
        if (change === 'totalRepayment') {
           console.log('ERROR: mutual exclusion bug on totalRepayment');
           break;
        }
        newVal = ($state.loanAmount + $state.totalInterest);
        if (newVal !== $state.totalRepayment) {
          let elt = document.getElementById('totalRepayment_widget_1809');
          $state.totalRepayment = newVal;
          
          elt.value = newVal;
          
         change = 'totalRepayment';
       }
     }
     
     
     div = document.getElementById('monthlyPayment_div_1892');
     div.style.display = true ? 'block' : 'none'; 
     
     if (true) {
        if (change === 'monthlyPayment') {
           console.log('ERROR: mutual exclusion bug on monthlyPayment');
           break;
        }
        newVal = (function () { const div = (($state.loanTerm * 12)); return div !== 0 ? Math.ceil($state.totalRepayment / div) : 0; })();
        if (newVal !== $state.monthlyPayment) {
          let elt = document.getElementById('monthlyPayment_widget_1892');
          $state.monthlyPayment = newVal;
          
          elt.value = newVal;
          
         change = 'monthlyPayment';
       }
     }
     
     
     div = document.getElementById('loanApproved_div_1984');
     div.style.display = true ? 'block' : 'none'; 
     
     if (true) {
        if (change === 'loanApproved') {
           console.log('ERROR: mutual exclusion bug on loanApproved');
           break;
        }
        newVal = (((($state.income > 50000) && ($state.monthlyDebts < 10000))) || $state.hasCoSigner);
        if (newVal !== $state.loanApproved) {
          let elt = document.getElementById('loanApproved_widget_1984');
          $state.loanApproved = newVal;
          
          elt.checked = newVal;
          
         change = 'loanApproved';
       }
     }
     
     
     div = document.getElementById('approvalMessage_div_2114');
     div.style.display = (true && $state.loanApproved) ? 'block' : 'none'; 
     
     if ((true && $state.loanApproved)) {
        if (change === 'approvalMessage') {
           console.log('ERROR: mutual exclusion bug on approvalMessage');
           break;
        }
        newVal = "Approved";
        if (newVal !== $state.approvalMessage) {
          let elt = document.getElementById('approvalMessage_widget_2114');
          $state.approvalMessage = newVal;
          
          elt.value = newVal;
          
         change = 'approvalMessage';
       }
     }
     
     
     div = document.getElementById('not_approvalMessage_div_2214');
     div.style.display = (true && !(($state.loanApproved))) ? 'block' : 'none'; 
     
     if ((true && !(($state.loanApproved)))) {
        if (change === 'not_approvalMessage') {
           console.log('ERROR: mutual exclusion bug on not_approvalMessage');
           break;
        }
        newVal = "Not Approved";
        if (newVal !== $state.not_approvalMessage) {
          let elt = document.getElementById('not_approvalMessage_widget_2214');
          $state.not_approvalMessage = newVal;
          
          elt.value = newVal;
          
         change = 'not_approvalMessage';
       }
     }
     
     
   } while (change !== '');
}
