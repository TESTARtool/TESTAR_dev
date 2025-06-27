var $state = {
  
  hasMaintLoan: false,
  
  hasSoldHouse: false,
  
  privateDebt: 0,
  
  sellingPrice: 0,
  
  valueResidue: 0,
  
  hasBoughtHouse: false,
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('hasBoughtHouse_widget_31');
     
     elt.checked = $state.hasBoughtHouse;
     
     div = document.getElementById('hasBoughtHouse_div_31');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('hasMaintLoan_widget_95');
     
     elt.checked = $state.hasMaintLoan;
     
     div = document.getElementById('hasMaintLoan_div_95');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('hasSoldHouse_widget_152');
     
     elt.checked = $state.hasSoldHouse;
     
     div = document.getElementById('hasSoldHouse_div_152');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('sellingPrice_widget_244');
     
     elt.value = $state.sellingPrice;
     
     div = document.getElementById('sellingPrice_div_244');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
   
     elt = document.getElementById('privateDebt_widget_306');
     
     elt.value = $state.privateDebt;
     
     div = document.getElementById('privateDebt_div_306');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
   
     elt = document.getElementById('valueResidue_widget_373');
     
     elt.value = $state.valueResidue;
     
     div = document.getElementById('valueResidue_div_373');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('hasBoughtHouse_div_31');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('hasMaintLoan_div_95');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('hasSoldHouse_div_152');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('sellingPrice_div_244');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
     
     
     div = document.getElementById('privateDebt_div_306');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
     
     
     div = document.getElementById('valueResidue_div_373');
     div.style.display = (true && $state.hasSoldHouse) ? 'block' : 'none'; 
     
     if ((true && $state.hasSoldHouse)) {
        if (change === 'valueResidue') {
           console.log('ERROR: mutual exclusion bug on valueResidue');
           break;
        }
        newVal = ($state.sellingPrice - $state.privateDebt);
        if (newVal !== $state.valueResidue) {
          let elt = document.getElementById('valueResidue_widget_373');
          $state.valueResidue = newVal;
          
          elt.value = newVal;
          
         change = 'valueResidue';
       }
     }
     
     
   } while (change !== '');
}
