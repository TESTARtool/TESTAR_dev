var $state = {
  
  highRemark: '',
  
  theAge2: 0,
  
  hoursAWeek: 0,
  
  ofAge: false,
  
  theAge: 0,
  
  theAgeToYoung: false,
  
  hourlyRate: 0,
  
  lowRemark: '',
  
  averageRemark: '',
  
  ofAge2: false,
  
  weeklyIncome: 0,
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('theAge_widget_17');
     
     elt.value = $state.theAge;
     
     div = document.getElementById('theAge_div_17');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('theAge2_widget_92');
     
     elt.value = $state.theAge2;
     
     div = document.getElementById('theAge2_div_92');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('ofAge_widget_178');
     
     elt.checked = $state.ofAge;
     
     div = document.getElementById('ofAge_div_178');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('ofAge2_widget_282');
     
     elt.checked = $state.ofAge2;
     
     div = document.getElementById('ofAge2_div_282');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('hourlyRate_widget_533');
     
     elt.value = $state.hourlyRate;
     
     div = document.getElementById('hourlyRate_div_533');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
   
     elt = document.getElementById('lowRemark_widget_688');
     
     elt.value = $state.lowRemark;
     
     div = document.getElementById('lowRemark_div_688');
     div.style.display = ((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && ($state.hourlyRate <= 5)) ? 'block' : 'none'; 
   
     elt = document.getElementById('averageRemark_widget_801');
     
     elt.value = $state.averageRemark;
     
     div = document.getElementById('averageRemark_div_801');
     div.style.display = (((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && !((($state.hourlyRate <= 5)))) && ($state.hourlyRate <= 15)) ? 'block' : 'none'; 
   
     elt = document.getElementById('highRemark_widget_893');
     
     elt.value = $state.highRemark;
     
     div = document.getElementById('highRemark_div_893');
     div.style.display = (((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && !((($state.hourlyRate <= 5)))) && !((($state.hourlyRate <= 15)))) ? 'block' : 'none'; 
   
     elt = document.getElementById('hoursAWeek_widget_979');
     
     elt.value = $state.hoursAWeek;
     
     div = document.getElementById('hoursAWeek_div_979');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
   
     elt = document.getElementById('weeklyIncome_widget_1050');
     
     elt.value = $state.weeklyIncome;
     
     div = document.getElementById('weeklyIncome_div_1050');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
   
     elt = document.getElementById('theAgeToYoung_widget_1200');
     
     elt.checked = $state.theAgeToYoung;
     
     div = document.getElementById('theAgeToYoung_div_1200');
     div.style.display = (true && !(((((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))))) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('theAge_div_17');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('theAge2_div_92');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('ofAge_div_178');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('ofAge2_div_282');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('hourlyRate_div_533');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('lowRemark_div_688');
     div.style.display = ((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && ($state.hourlyRate <= 5)) ? 'block' : 'none'; 
     
     
     div = document.getElementById('averageRemark_div_801');
     div.style.display = (((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && !((($state.hourlyRate <= 5)))) && ($state.hourlyRate <= 15)) ? 'block' : 'none'; 
     
     
     div = document.getElementById('highRemark_div_893');
     div.style.display = (((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) && !((($state.hourlyRate <= 5)))) && !((($state.hourlyRate <= 15)))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('hoursAWeek_div_979');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('weeklyIncome_div_1050');
     div.style.display = (true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))) ? 'block' : 'none'; 
     
     if ((true && (((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2)))) {
        if (change === 'weeklyIncome') {
           console.log('ERROR: mutual exclusion bug on weeklyIncome');
           break;
        }
        newVal = ($state.hourlyRate * $state.hoursAWeek);
        if (newVal !== $state.weeklyIncome) {
          let elt = document.getElementById('weeklyIncome_widget_1050');
          $state.weeklyIncome = newVal;
          
          elt.value = newVal;
          
         change = 'weeklyIncome';
       }
     }
     
     
     div = document.getElementById('theAgeToYoung_div_1200');
     div.style.display = (true && !(((((($state.theAge >= 18) && ($state.ofAge === true)) && ($state.ofAge === $state.ofAge2)) && ($state.theAge === $state.theAge2))))) ? 'block' : 'none'; 
     
     
   } while (change !== '');
}
