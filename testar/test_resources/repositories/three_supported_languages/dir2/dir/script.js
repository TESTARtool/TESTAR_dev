//code from https://github.com/ahoef/javascript-calculator/blob/master/js/site.js
$(document).ready(function(){
  var numOne;
  var numTwo;
  var operator;
  var $display = $(".total");

  // Clear variables and set display to '0'
  function reset() {
    numOne = null;
    numTwo = null;
    operator = null;
    $display.text("0");
  }

  reset();

  // Set a 12-digit max on the display
  function testNumLength(number) {
      if (number.length > 12) {
        $display.text("too long!");
      }
  };

  // Display the first number in the equation by setting
  // a newVal variable to the first digit clicked, and
  // concatenating if there are multiple digits/clicks
  $(".numbers a").click(function() {
    var clickDigit = $(this).text();
    var currentVal = $display.text();
    var newVal;
    if (currentVal === "0") {
      newVal = clickDigit;
    } else {
      newVal = currentVal + clickDigit;
    }
    $display.text(newVal);
    testNumLength($display.text());
  });

  // Store the operator clicked, set the numOne variable
  // by turning a string (the digits in the display) into
  // a number to be calculated in the math equation, and
  // set the display back to '0'
  $(".operators a").click(function(){
    operator = $(this).text();
    numOne = parseFloat($display.text());
    $display.text("0");
  });

  // Set the numTwo variable by getting the display value
  // of the second set of digits clicked, and turn it into
  // a number. Perform math calculations conditionally
  // according the the operator value, and display the total
  // if it is shorter than 12 digits long.
  $("#equals").click(function(){
    var total;

    numTwo = parseFloat($display.text());

    if (operator === "+"){
      total = numOne + numTwo;
    }
    else if (operator === "-"){
      total = numOne - numTwo;
    }
    else if (operator === "/"){
      total = numOne / numTwo;
    }
    else if (operator === "*"){
      total = numOne * numTwo;
    }

    $display.text(total);
    testNumLength($display.text());
  });

  // Call reset function
  $("#clear").click(function(){
    reset();
  });
});