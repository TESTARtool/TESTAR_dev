var $state = {
  
  integerWidget: 0,
  
  monthWidget: '',
  
  colorWidget: '#ffffff',
  
  booleanWidget: false,
  
  moreOfWidget: '',
  
  timeWidget: '',
  
  dateWidget: '',
  
  stringWidget: '',
  
  weekWidget: '',
  
  blue: '',
  
  rangeWidget: 0,
  
  oneOfWidget: '',
  
}
function $update(name, value) {
   let change = '';
   let newVal = undefined;
   let div = undefined;
   if (name === undefined) {
     let elt = null;
     let div = null;
   
     elt = document.getElementById('booleanWidget_widget_21');
     
     elt.checked = $state.booleanWidget;
     
     div = document.getElementById('booleanWidget_div_21');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('integerWidget_widget_58');
     
     elt.value = $state.integerWidget;
     
     div = document.getElementById('integerWidget_div_58');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('stringWidget_widget_95');
     
     elt.value = $state.stringWidget;
     
     div = document.getElementById('stringWidget_div_95');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('colorWidget_widget_130');
     
     elt.value = $state.colorWidget;
     
     div = document.getElementById('colorWidget_div_130');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('dateWidget_widget_161');
     
     elt.value = $state.dateWidget;
     
     div = document.getElementById('dateWidget_div_161');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('weekWidget_widget_189');
     
     elt.value = $state.weekWidget;
     
     div = document.getElementById('weekWidget_div_189');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('monthWidget_widget_217');
     
     elt.value = $state.monthWidget;
     
     div = document.getElementById('monthWidget_div_217');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('timeWidget_widget_248');
     
     elt.value = $state.timeWidget;
     
     div = document.getElementById('timeWidget_div_248');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('rangeWidget_widget_276');
     
     elt.value = $state.rangeWidget;
     
     div = document.getElementById('rangeWidget_div_276');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('oneOfWidget_widget_319');
     
     elt.value = $state.oneOfWidget;
     
     div = document.getElementById('oneOfWidget_div_319');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('moreOfWidget_widget_377');
     
     elt.value = $state.moreOfWidget;
     
     div = document.getElementById('moreOfWidget_div_377');
     div.style.display = true ? 'block' : 'none'; 
   
     elt = document.getElementById('blue_widget_484');
     
     elt.value = $state.blue;
     
     div = document.getElementById('blue_div_484');
     div.style.display = (true && ($state.oneOfWidget === "Blue")) ? 'block' : 'none'; 
   
     return;
   }
   $state[name] = value;
   do {
     change = '';
     
     div = document.getElementById('booleanWidget_div_21');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('integerWidget_div_58');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('stringWidget_div_95');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('colorWidget_div_130');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('dateWidget_div_161');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('weekWidget_div_189');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('monthWidget_div_217');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('timeWidget_div_248');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('rangeWidget_div_276');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('oneOfWidget_div_319');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('moreOfWidget_div_377');
     div.style.display = true ? 'block' : 'none'; 
     
     
     div = document.getElementById('blue_div_484');
     div.style.display = (true && ($state.oneOfWidget === "Blue")) ? 'block' : 'none'; 
     
     
   } while (change !== '');
}
