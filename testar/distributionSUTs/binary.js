var $state = {
  
  x_1_5: false,
  
  x_3_4: false,
  
  answer_1_2: 0,
  
  answer_3_4: 0,
  
  answer_6_7: 0,
  
  answer_7_8: 0,
  
  answer_8_9: 0,
  
  x_1_3: false,
  
  x_5_6: false,
  
  answer_2_3: 0,
  
  x_5_7: false,
  
  answer_9_10: 0,
  
  x_7_8: false,
  
  answer_4_5: 0,
  
  x_8_9: false,
  
  answer_5_6: 0,
  
  x_1_10: false,
  
  answer_19_20: 0,
  
  x_17_18: false,
  
  x_18_19: false,
  
  x_10_11: false,
  
  x_12_13: false,
  
  x_13_14: false,
  
  x_15_16: false,
  
  x_10_12: false,
  
  x_15_17: false,
  
  x_10_15: false,
  
  x_1_2: false,
  
  answer_10_11: 0,
  
  answer_11_12: 0,
  
  answer_12_13: 0,
  
  answer_13_14: 0,
  
  answer_14_15: 0,
  
  answer_15_16: 0,
  
  answer_16_17: 0,
  
  answer_17_18: 0,
  
  answer_18_19: 0,
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('x_1_10_widget_25');
     
     elt.checked = $state.x_1_10;
     
     div = document.getElementById('x_1_10_div_25');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('x_1_5_widget_95');
     
     elt.checked = $state.x_1_5;
     
     div = document.getElementById('x_1_5_div_95');
     div.style.display = (true && $state.x_1_10) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_1_3_widget_166');
     
     elt.checked = $state.x_1_3;
     
     div = document.getElementById('x_1_3_div_166');
     div.style.display = ((true && $state.x_1_10) && $state.x_1_5) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_1_2_widget_241');
     
     elt.checked = $state.x_1_2;
     
     div = document.getElementById('x_1_2_div_241');
     div.style.display = (((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_1_2_widget_320');
     
     elt.value = $state.answer_1_2;
     
     div = document.getElementById('answer_1_2_div_320');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && $state.x_1_2) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_2_3_widget_397');
     
     elt.value = $state.answer_2_3;
     
     div = document.getElementById('answer_2_3_div_397');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && !(($state.x_1_2))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_3_4_widget_479');
     
     elt.checked = $state.x_3_4;
     
     div = document.getElementById('x_3_4_div_479');
     div.style.display = (((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_3_4_widget_558');
     
     elt.value = $state.answer_3_4;
     
     div = document.getElementById('answer_3_4_div_558');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && $state.x_3_4) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_4_5_widget_636');
     
     elt.value = $state.answer_4_5;
     
     div = document.getElementById('answer_4_5_div_636');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && !(($state.x_3_4))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_5_7_widget_720');
     
     elt.checked = $state.x_5_7;
     
     div = document.getElementById('x_5_7_div_720');
     div.style.display = ((true && $state.x_1_10) && !(($state.x_1_5))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_5_6_widget_795');
     
     elt.checked = $state.x_5_6;
     
     div = document.getElementById('x_5_6_div_795');
     div.style.display = (((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_5_6_widget_874');
     
     elt.value = $state.answer_5_6;
     
     div = document.getElementById('answer_5_6_div_874');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && $state.x_5_6) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_6_7_widget_952');
     
     elt.value = $state.answer_6_7;
     
     div = document.getElementById('answer_6_7_div_952');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && !(($state.x_5_6))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_7_8_widget_1034');
     
     elt.checked = $state.x_7_8;
     
     div = document.getElementById('x_7_8_div_1034');
     div.style.display = (((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_7_8_widget_1113');
     
     elt.value = $state.answer_7_8;
     
     div = document.getElementById('answer_7_8_div_1113');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && $state.x_7_8) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_8_9_widget_1191');
     
     elt.checked = $state.x_8_9;
     
     div = document.getElementById('x_8_9_div_1191');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_8_9_widget_1274');
     
     elt.value = $state.answer_8_9;
     
     div = document.getElementById('answer_8_9_div_1274');
     div.style.display = (((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && $state.x_8_9) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_9_10_widget_1358');
     
     elt.value = $state.answer_9_10;
     
     div = document.getElementById('answer_9_10_div_1358');
     div.style.display = (((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && !(($state.x_8_9))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_10_15_widget_1455');
     
     elt.checked = $state.x_10_15;
     
     div = document.getElementById('x_10_15_div_1455');
     div.style.display = (true && !(($state.x_1_10))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_10_12_widget_1532');
     
     elt.checked = $state.x_10_12;
     
     div = document.getElementById('x_10_12_div_1532');
     div.style.display = ((true && !(($state.x_1_10))) && $state.x_10_15) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_10_11_widget_1613');
     
     elt.checked = $state.x_10_11;
     
     div = document.getElementById('x_10_11_div_1613');
     div.style.display = (((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_10_11_widget_1698');
     
     elt.value = $state.answer_10_11;
     
     div = document.getElementById('answer_10_11_div_1698');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && $state.x_10_11) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_11_12_widget_1779');
     
     elt.value = $state.answer_11_12;
     
     div = document.getElementById('answer_11_12_div_1779');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && !(($state.x_10_11))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_12_13_widget_1864');
     
     elt.checked = $state.x_12_13;
     
     div = document.getElementById('x_12_13_div_1864');
     div.style.display = (((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_12_13_widget_1949');
     
     elt.value = $state.answer_12_13;
     
     div = document.getElementById('answer_12_13_div_1949');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && $state.x_12_13) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_13_14_widget_2030');
     
     elt.checked = $state.x_13_14;
     
     div = document.getElementById('x_13_14_div_2030');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_13_14_widget_2119');
     
     elt.value = $state.answer_13_14;
     
     div = document.getElementById('answer_13_14_div_2119');
     div.style.display = (((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && $state.x_13_14) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_14_15_widget_2206');
     
     elt.value = $state.answer_14_15;
     
     div = document.getElementById('answer_14_15_div_2206');
     div.style.display = (((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && !(($state.x_13_14))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_15_17_widget_2305');
     
     elt.checked = $state.x_15_17;
     
     div = document.getElementById('x_15_17_div_2305');
     div.style.display = ((true && !(($state.x_1_10))) && !(($state.x_10_15))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_15_16_widget_2386');
     
     elt.checked = $state.x_15_16;
     
     div = document.getElementById('x_15_16_div_2386');
     div.style.display = (((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_15_16_widget_2471');
     
     elt.value = $state.answer_15_16;
     
     div = document.getElementById('answer_15_16_div_2471');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && $state.x_15_16) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_16_17_widget_2552');
     
     elt.value = $state.answer_16_17;
     
     div = document.getElementById('answer_16_17_div_2552');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && !(($state.x_15_16))) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_17_18_widget_2637');
     
     elt.checked = $state.x_17_18;
     
     div = document.getElementById('x_17_18_div_2637');
     div.style.display = (((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_17_18_widget_2722');
     
     elt.value = $state.answer_17_18;
     
     div = document.getElementById('answer_17_18_div_2722');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && $state.x_17_18) ? 'block' : 'none'; 
   
     elt = document.getElementById('x_18_19_widget_2803');
     
     elt.checked = $state.x_18_19;
     
     div = document.getElementById('x_18_19_div_2803');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_18_19_widget_2892');
     
     elt.value = $state.answer_18_19;
     
     div = document.getElementById('answer_18_19_div_2892');
     div.style.display = (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && $state.x_18_19) ? 'block' : 'none'; 
   
     elt = document.getElementById('answer_19_20_widget_2979');
     
     elt.value = $state.answer_19_20;
     
     div = document.getElementById('answer_19_20_div_2979');
     div.style.display = (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && !(($state.x_18_19))) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('x_1_10_div_25');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_1_5_div_95');
     div.style.display = (true && $state.x_1_10) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_1_3_div_166');
     div.style.display = ((true && $state.x_1_10) && $state.x_1_5) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_1_2_div_241');
     div.style.display = (((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_1_2_div_320');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && $state.x_1_2) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && $state.x_1_2)) {
        if (change === 'answer_1_2') {
           console.log('ERROR: mutual exclusion bug on answer_1_2');
           break;
        }
        newVal = (1);
        if (newVal !== $state.answer_1_2) {
          let elt = document.getElementById('answer_1_2_widget_320');
          $state.answer_1_2 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_1_2';
       }
     }
     
     
     div = document.getElementById('answer_2_3_div_397');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && !(($state.x_1_2))) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && $state.x_1_5) && $state.x_1_3) && !(($state.x_1_2)))) {
        if (change === 'answer_2_3') {
           console.log('ERROR: mutual exclusion bug on answer_2_3');
           break;
        }
        newVal = (2);
        if (newVal !== $state.answer_2_3) {
          let elt = document.getElementById('answer_2_3_widget_397');
          $state.answer_2_3 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_2_3';
       }
     }
     
     
     div = document.getElementById('x_3_4_div_479');
     div.style.display = (((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_3_4_div_558');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && $state.x_3_4) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && $state.x_3_4)) {
        if (change === 'answer_3_4') {
           console.log('ERROR: mutual exclusion bug on answer_3_4');
           break;
        }
        newVal = (3);
        if (newVal !== $state.answer_3_4) {
          let elt = document.getElementById('answer_3_4_widget_558');
          $state.answer_3_4 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_3_4';
       }
     }
     
     
     div = document.getElementById('answer_4_5_div_636');
     div.style.display = ((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && !(($state.x_3_4))) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && $state.x_1_5) && !(($state.x_1_3))) && !(($state.x_3_4)))) {
        if (change === 'answer_4_5') {
           console.log('ERROR: mutual exclusion bug on answer_4_5');
           break;
        }
        newVal = (4);
        if (newVal !== $state.answer_4_5) {
          let elt = document.getElementById('answer_4_5_widget_636');
          $state.answer_4_5 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_4_5';
       }
     }
     
     
     div = document.getElementById('x_5_7_div_720');
     div.style.display = ((true && $state.x_1_10) && !(($state.x_1_5))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_5_6_div_795');
     div.style.display = (((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_5_6_div_874');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && $state.x_5_6) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && $state.x_5_6)) {
        if (change === 'answer_5_6') {
           console.log('ERROR: mutual exclusion bug on answer_5_6');
           break;
        }
        newVal = (5);
        if (newVal !== $state.answer_5_6) {
          let elt = document.getElementById('answer_5_6_widget_874');
          $state.answer_5_6 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_5_6';
       }
     }
     
     
     div = document.getElementById('answer_6_7_div_952');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && !(($state.x_5_6))) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && !(($state.x_1_5))) && $state.x_5_7) && !(($state.x_5_6)))) {
        if (change === 'answer_6_7') {
           console.log('ERROR: mutual exclusion bug on answer_6_7');
           break;
        }
        newVal = (6);
        if (newVal !== $state.answer_6_7) {
          let elt = document.getElementById('answer_6_7_widget_952');
          $state.answer_6_7 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_6_7';
       }
     }
     
     
     div = document.getElementById('x_7_8_div_1034');
     div.style.display = (((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_7_8_div_1113');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && $state.x_7_8) ? 'block' : 'none'; 
     
     if (((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && $state.x_7_8)) {
        if (change === 'answer_7_8') {
           console.log('ERROR: mutual exclusion bug on answer_7_8');
           break;
        }
        newVal = (7);
        if (newVal !== $state.answer_7_8) {
          let elt = document.getElementById('answer_7_8_widget_1113');
          $state.answer_7_8 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_7_8';
       }
     }
     
     
     div = document.getElementById('x_8_9_div_1191');
     div.style.display = ((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_8_9_div_1274');
     div.style.display = (((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && $state.x_8_9) ? 'block' : 'none'; 
     
     if ((((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && $state.x_8_9)) {
        if (change === 'answer_8_9') {
           console.log('ERROR: mutual exclusion bug on answer_8_9');
           break;
        }
        newVal = (8);
        if (newVal !== $state.answer_8_9) {
          let elt = document.getElementById('answer_8_9_widget_1274');
          $state.answer_8_9 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_8_9';
       }
     }
     
     
     div = document.getElementById('answer_9_10_div_1358');
     div.style.display = (((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && !(($state.x_8_9))) ? 'block' : 'none'; 
     
     if ((((((true && $state.x_1_10) && !(($state.x_1_5))) && !(($state.x_5_7))) && !(($state.x_7_8))) && !(($state.x_8_9)))) {
        if (change === 'answer_9_10') {
           console.log('ERROR: mutual exclusion bug on answer_9_10');
           break;
        }
        newVal = (9);
        if (newVal !== $state.answer_9_10) {
          let elt = document.getElementById('answer_9_10_widget_1358');
          $state.answer_9_10 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_9_10';
       }
     }
     
     
     div = document.getElementById('x_10_15_div_1455');
     div.style.display = (true && !(($state.x_1_10))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_10_12_div_1532');
     div.style.display = ((true && !(($state.x_1_10))) && $state.x_10_15) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_10_11_div_1613');
     div.style.display = (((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_10_11_div_1698');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && $state.x_10_11) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && $state.x_10_11)) {
        if (change === 'answer_10_11') {
           console.log('ERROR: mutual exclusion bug on answer_10_11');
           break;
        }
        newVal = (10);
        if (newVal !== $state.answer_10_11) {
          let elt = document.getElementById('answer_10_11_widget_1698');
          $state.answer_10_11 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_10_11';
       }
     }
     
     
     div = document.getElementById('answer_11_12_div_1779');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && !(($state.x_10_11))) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && $state.x_10_15) && $state.x_10_12) && !(($state.x_10_11)))) {
        if (change === 'answer_11_12') {
           console.log('ERROR: mutual exclusion bug on answer_11_12');
           break;
        }
        newVal = (11);
        if (newVal !== $state.answer_11_12) {
          let elt = document.getElementById('answer_11_12_widget_1779');
          $state.answer_11_12 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_11_12';
       }
     }
     
     
     div = document.getElementById('x_12_13_div_1864');
     div.style.display = (((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_12_13_div_1949');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && $state.x_12_13) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && $state.x_12_13)) {
        if (change === 'answer_12_13') {
           console.log('ERROR: mutual exclusion bug on answer_12_13');
           break;
        }
        newVal = (12);
        if (newVal !== $state.answer_12_13) {
          let elt = document.getElementById('answer_12_13_widget_1949');
          $state.answer_12_13 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_12_13';
       }
     }
     
     
     div = document.getElementById('x_13_14_div_2030');
     div.style.display = ((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_13_14_div_2119');
     div.style.display = (((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && $state.x_13_14) ? 'block' : 'none'; 
     
     if ((((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && $state.x_13_14)) {
        if (change === 'answer_13_14') {
           console.log('ERROR: mutual exclusion bug on answer_13_14');
           break;
        }
        newVal = (13);
        if (newVal !== $state.answer_13_14) {
          let elt = document.getElementById('answer_13_14_widget_2119');
          $state.answer_13_14 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_13_14';
       }
     }
     
     
     div = document.getElementById('answer_14_15_div_2206');
     div.style.display = (((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && !(($state.x_13_14))) ? 'block' : 'none'; 
     
     if ((((((true && !(($state.x_1_10))) && $state.x_10_15) && !(($state.x_10_12))) && !(($state.x_12_13))) && !(($state.x_13_14)))) {
        if (change === 'answer_14_15') {
           console.log('ERROR: mutual exclusion bug on answer_14_15');
           break;
        }
        newVal = (14);
        if (newVal !== $state.answer_14_15) {
          let elt = document.getElementById('answer_14_15_widget_2206');
          $state.answer_14_15 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_14_15';
       }
     }
     
     
     div = document.getElementById('x_15_17_div_2305');
     div.style.display = ((true && !(($state.x_1_10))) && !(($state.x_10_15))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('x_15_16_div_2386');
     div.style.display = (((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_15_16_div_2471');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && $state.x_15_16) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && $state.x_15_16)) {
        if (change === 'answer_15_16') {
           console.log('ERROR: mutual exclusion bug on answer_15_16');
           break;
        }
        newVal = (15);
        if (newVal !== $state.answer_15_16) {
          let elt = document.getElementById('answer_15_16_widget_2471');
          $state.answer_15_16 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_15_16';
       }
     }
     
     
     div = document.getElementById('answer_16_17_div_2552');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && !(($state.x_15_16))) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && $state.x_15_17) && !(($state.x_15_16)))) {
        if (change === 'answer_16_17') {
           console.log('ERROR: mutual exclusion bug on answer_16_17');
           break;
        }
        newVal = (16);
        if (newVal !== $state.answer_16_17) {
          let elt = document.getElementById('answer_16_17_widget_2552');
          $state.answer_16_17 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_16_17';
       }
     }
     
     
     div = document.getElementById('x_17_18_div_2637');
     div.style.display = (((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_17_18_div_2722');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && $state.x_17_18) ? 'block' : 'none'; 
     
     if (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && $state.x_17_18)) {
        if (change === 'answer_17_18') {
           console.log('ERROR: mutual exclusion bug on answer_17_18');
           break;
        }
        newVal = (17);
        if (newVal !== $state.answer_17_18) {
          let elt = document.getElementById('answer_17_18_widget_2722');
          $state.answer_17_18 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_17_18';
       }
     }
     
     
     div = document.getElementById('x_18_19_div_2803');
     div.style.display = ((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) ? 'block' : 'none'; 
     
     
     div = document.getElementById('answer_18_19_div_2892');
     div.style.display = (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && $state.x_18_19) ? 'block' : 'none'; 
     
     if ((((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && $state.x_18_19)) {
        if (change === 'answer_18_19') {
           console.log('ERROR: mutual exclusion bug on answer_18_19');
           break;
        }
        newVal = (18);
        if (newVal !== $state.answer_18_19) {
          let elt = document.getElementById('answer_18_19_widget_2892');
          $state.answer_18_19 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_18_19';
       }
     }
     
     
     div = document.getElementById('answer_19_20_div_2979');
     div.style.display = (((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && !(($state.x_18_19))) ? 'block' : 'none'; 
     
     if ((((((true && !(($state.x_1_10))) && !(($state.x_10_15))) && !(($state.x_15_17))) && !(($state.x_17_18))) && !(($state.x_18_19)))) {
        if (change === 'answer_19_20') {
           console.log('ERROR: mutual exclusion bug on answer_19_20');
           break;
        }
        newVal = (19);
        if (newVal !== $state.answer_19_20) {
          let elt = document.getElementById('answer_19_20_widget_2979');
          $state.answer_19_20 = newVal;
          
          elt.value = newVal;
          
         change = 'answer_19_20';
       }
     }
     
     
   } while (change !== '');
}
