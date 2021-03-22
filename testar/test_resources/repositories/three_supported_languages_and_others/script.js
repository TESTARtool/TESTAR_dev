//script from https://github.com/rickMcGavin/JavaScript-Calculator/tree/master/js
// declaring functions
(function() {
  function Calculator(displayId) {
    this.displayId = displayId;
    this.arr = [];
  }

  Calculator.prototype.updateDisplay = function() {
    document.getElementById(this.displayId).innerText = this.arr.join(" ");
  };

  Calculator.prototype.handleNumber = function(number) {
    if(isNaN(this.arr[this.arr.length - 1])) {
      this.arr.push(number.toString());
    } else {
      this.arr[this.arr.length - 1] += number.toString();
    }
    this.updateDisplay();
  };

  Calculator.prototype.handleOperator = function(operator) {
    if (!isNaN(this.arr[this.arr.length - 1])) {
      if (operator === ".") {
        if (!this.arr[this.arr.length - 1].includes(".")) {
          this.arr[this.arr.length - 1] += operator.toString();
        }
      } else {
        this.arr.push(operator);
      }
      this.updateDisplay();
    }
  };

  Calculator.prototype.allClear = function(funcName) {
    this.arr = [];
    this.updateDisplay();
  };

  Calculator.prototype.clearEntry = function() {
    this.arr.pop();
    this.updateDisplay();
  };

  Calculator.prototype.sumNumbers = function() {
    var total = eval(this.arr.join(" "));
    this.arr = [total];
    this.updateDisplay();
  };

    // declaring needed variables
  var calc = new Calculator("display");
  var numberButtons = document.getElementsByClassName("number-button");
  var operatorButtons = document.getElementsByClassName("operator-button");
  var allClearButton = document.querySelector(".all-clear");
  var clearEntryButton = document.querySelector(".clear-entry");
  var equalsButton = document.querySelector(".equals-button");


  allClearButton.addEventListener("click", function() {
    calc.allClear();
  });

  clearEntryButton.addEventListener("click", function() {
    calc.clearEntry();
  });

  equalsButton.addEventListener("click", function() {
    calc.sumNumbers();
  });

  for (var i = 0; i < operatorButtons.length; i++) {
    operatorButtons[i].addEventListener("click", function() {
      calc.handleOperator(this.textContent);
    });
  }

  for (var j = 0; j < numberButtons.length; j++) {
    numberButtons[j].addEventListener("click", function() {
      calc.handleNumber(this.textContent);
    });
  }

  // key controls
  window.onkeyup = function(event) {
    /* Below, I broke the number 8 out separately from the rest because the asterisk and the number 8 share the same keycode, and it was allowing the asterisk to be entered in to the array on the end of a number */
    if ((event.keyCode >= 48 && event.keyCode <= 55)  ||
        (event.key === "8") || (event.keyCode === 57) ||
        (event.keyCode >= 96 && event.keyCode <= 105)) {
      calc.handleNumber(event.key);           // numbers
    } else if (event.keyCode === 27) {
      calc.allClear();                        // escape = all clear
    } else if (event.keyCode === 8) {
      calc.clearEntry();                      // backspace/delete = clear entry
    } else if (event.key === "+") {
      calc.handleOperator(event.key);         // addition operator
    } else if (event.key === "-") {
      calc.handleOperator(event.key);         // subtraction operator
    } else if (event.key === "*") {
      calc.handleOperator(event.key);         // multiplication operator
    } else if (event.key === "/") {
      calc.handleOperator(event.key);         // division operator
    } else if (event.key === ".") {
      calc.handleOperator(event.key);         // decimal
    } else if (event.keyCode === 13) {
      calc.sumNumbers();                       // equals/sum operation
    }
  };
})();