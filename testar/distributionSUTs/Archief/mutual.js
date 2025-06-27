var $state = {
  
  num: 0,
  
  x: false,
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('num_widget_31');
     
     elt.value = $state.num;
     
     div = document.getElementById('num_div_31');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('x_widget_84');
     
     elt.checked = $state.x;
     
     div = document.getElementById('x_div_84');
     div.style.display = (true && ($state.num >= 0)) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_widget_134');
     
     elt.checked = $state.x;
     
     div = document.getElementById('x_div_134');
     div.style.display = (true && ($state.num <= 0)) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('num_div_31');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_div_84');
     div.style.display = (true && ($state.num >= 0)) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_div_134');
     div.style.display = (true && ($state.num <= 0)) ? 'block' : 'none'; 
     
     if ((true && ($state.num <= 0))) {
        if (change === 'x') {
           console.log('ERROR: mutual exclusion bug on x');
           break;
        }
        newVal = true;
        if (newVal !== $state.x) {
          let elt = document.getElementById('x_widget_134');
          $state.x = newVal;
          
          elt.checked = newVal;
          
         change = 'x';
       }
     }
     
     
   } while (change !== '');
}
