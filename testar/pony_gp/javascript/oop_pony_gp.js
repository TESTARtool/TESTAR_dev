/**
 * A Tree has a root which is an object of class TreeNode.
 *
 * @param {TreeNode} root The root node of the tree
 * @constructor
 */
var Tree = function (root) {
    this.root = root;
    this.nodeCnt = 1;
    this.depth = 1;
};

/**
 * Copy the tree.
 *
 * @returns {Tree} A copy of the tree
 */
Tree.prototype.copy = function () {
    var root = this.root.copy();
    var tree = new Tree(root);
    tree.nodeCnt = this.nodeCnt;
    tree.depth = this.depth;
    return tree;
};

/**
 * Calculate max depth of tree and count the number of nodes.
 *
 * @returns {number} Max tree depth
 */
Tree.prototype.calculateDepth = function () {
    // Get a list of all nodes
    var allNodes = [];
    this.depthFirst(this.root, 0, allNodes);
    // Find depth of each node
    var nodeDepths = [];
    for (var i = 0; i < allNodes.length; i = i + 1) {
        nodeDepths.push(allNodes[i].depth);
    }
    // Node with maximum depth is the also the max depth of the tree
    this.depth = max(nodeDepths);
    this.nodeCnt = nodeDepths.length;
    return this.depth
};

/**
 * Recursively collect by pre-order depth-first left-to-right traversal.
 *
 * @param {TreeNode} root Start of traversal
 * @param {number} depth Depth of tree
 * @param {array} nodes The tree nodes and their depths in traversal order
 */
Tree.prototype.depthFirst = function (root, depth, nodes) {
    // Push node to the array
    nodes.push({node: root, depth: depth});
    /*
    Iterate over the children of the root node. If the node is a
    leaf node then it has no children and there will be no more
    calls to depth_first.
    */
    for (var i = 0; i < root.children.length; i = i + 1) {
        this.depthFirst(root.children[i], depth + 1, nodes);
    }
};

/**
 * A node in a tree. A node has a parent, a symbol as a label and children.
 *
 * @param {TreeNode} parent The parent of the node. Null indicates no parent,
 * i.e. root node
 * @param {string} symbol The label of the node
 * @constructor
 */
var TreeNode = function (parent, symbol) {
    this.parent = parent;
    this.symbol = symbol;
    this.children = [];
};

/**
 * Copy the tree node.
 *
 * @returns {TreeNode} A copy of the tree node.
 */
TreeNode.prototype.copy = function () {
    var copy = new TreeNode(this.parent, this.symbol);
    for (var i = 0; i < this.children.length; i = i + 1) {
        copy.children.push(this.children[i].copy());
    }
    return copy;
};

/**
 * Return an s-expression for the node and its descendants.
 *
 * @returns {string} S-expression of tree.
 */
TreeNode.prototype.strAsTree = function () {
    var str_ = "";
    // The number of children determines if it is it a internal or leaf node
    if (this.children.length > 0) {
        // Append a ( before the symbol to denote the start of a subtree
        str_ = "(" + this.symbol;
        for (var i = 0; i < this.children.length; i = i + 1) {
            str_ = str_ + " " + this.children[i].strAsTree();
        }
        // Append a ) to close the subtree
        str_ = str_ + ")";
    } else {
        str_ = this.symbol;
    }
    return str_;
};

/**
 * Recursively grow a node to max depth in a pre-order, i.e. depth-first
 * left-to-right traversal.
 *
 * @param {TreeNode} node Root node of subtree
 * @param {number} depth Current tree depth
 * @param {number} maxDepth Maximum tree depth
 * @param {boolean} full Grows the tree to max depth when true
 * @param {Symbols} symbols Set of symbols to chose from
 */
TreeNode.prototype.grow = function (node, depth, maxDepth, full, symbols) {
    /*
     grow is called recursively arity number of times from this
     loop. The arity is given by the node symbol
     */
    for (var i = 0; i < symbols.arities[node.symbol]; i = i + 1) {
        // Get a random symbol
        var symbol = symbols.getRandomSymbol(depth, maxDepth, full);
        // Create a child node
        var child = new TreeNode(node, symbol);
        // Append the child node to the current node
        node.children.push(child);
        // Call grow with the child node as the current node
        this.grow(child, depth + 1, maxDepth, full, symbols);
    }
};

/**
 * Symbols are functions (internal nodes) or terminals (leaves). Has the arity
 * for each symbol, the terminal symbols and the function symbols.
 *
 * @param {object} arities Dictionary with the symbol as a key and the arity of
 * each symbol as the value
 * @constructor
 */
var Symbols = function (arities) {
    this.arities = arities;
    this.terminals = [];
    this.functions = [];
    for (var symbol in this.arities) {
        // A symbol with arity 0 is a terminal
        if (this.arities[symbol] == 0) {
            this.terminals.push(symbol);
        } else {
            this.functions.push(symbol);
        }
    }
};

/**
 * Get a random label. The depth determines if a terminal
 * must be chosen. If full is specified a function will be chosen
 * until max_depth.
 *
 * @param {number} depth Current depth
 * @param {number} maxDepth Max allowed depth
 * @param {boolean} full Grow to fully to max depth
 * @returns {string} Symbol
 */
Symbols.prototype.getRandomSymbol = function (depth, maxDepth, full) {
    var symbol;
    // Pick a terminal if max depth is reached
    if (depth == maxDepth) {
        symbol = RNG.getRandomChoice(this.terminals);
    } else {
        // Pick function node if full or with 50% chance
        if (full || RNG.getRandomBoolean()) {
            symbol = RNG.getRandomChoice(this.functions);
        } else {
            symbol = RNG.getRandomChoice(this.terminals);
        }
    }
    return symbol
};

/**
 * A GP Individual. Has a genome and a fitness
 *
 * @param {Tree} genome A tree representing the individual solution
 * @constructor
 */
var Individual = function (genome) {
    this.genome = genome;
    this.fitness = DEFAULT_FITNESS;
};

/**
 * Copy an Individual. The fitness is reset.
 *
 * @returns {Individual} Copy of the individual
 */
Individual.prototype.copy = function () {
    var genome = this.genome.copy();
    return new Individual(genome);
};

/**
 * Fitness function for fitting data, i.e. supervised learning. Symbolic regression is a type of
 * regression analysis that searches the space of mathematical expressions to
 * find the model that best fits a given dataset.
 *
 * Evaluate fitness based on fitness cases and target values. Fitness cases are
 * a set of exemplars (input and output points) by comparing the error between
 * the output of an individual(symbolic expression) and the target values.
 *
 * @param {array} data Exemplars to evaluate individual solutions on. The last column
 * is the label
 * @constructor
 */
var SymbolicRegression = function (data) {
    this.data = data;
    this.labelCol = this.data[0].length - 1;
};

/**
 * Evaluate the individual solution (model) on all the data. Fitness is the
 * negative Mean Squared Error.
 *
 * @param {Individual} individual Individual solution to evaluate
 * @returns {number} Fitness, the negative MSE
 */
SymbolicRegression.prototype.evaluateIndividual = function (individual) {
    var fitness = 0.0;
    for (var i = 0; i < this.data.length; i++) {
        // Get the prediction of the model based on the data
        var output = this.evaluate(individual.genome.root, this.data[i]);
        // Get the observed value based on the data
        var target = this.data[i][this.labelCol];
        // Get the error
        var error = output - target;
        // Fitness is the MSE
        fitness += Math.pow(error, 2);
    }
    // Negate the fitness, we are maximising
    fitness = -fitness / this.data.length;
    if (isNaN(fitness)) {
        fitness = DEFAULT_FITNESS;
    }
    return fitness;
};

/**
 * Assign fitness to each individual solution in the population.
 *
 * @param {array} Population List of Individuals
 * @returns {array} Evaluated population
 */
SymbolicRegression.prototype.evaluatePopulation = function (population) {
    for (var i = 0; i < population.length; i = i + 1) {
        population[i]["fitness"] = this.evaluateIndividual(population[i]);
    }
    return population;
};

/**
 * Evaluate the model (f) on the input data (x_0,...,x_n), i.e. f(x_0,...,x_n).
 * Evaluate a node recursively. The node's symbol is evaluated.
 *
 * @param {TreeNode} node Node that is evaluated
 * @param {array} inputData Data of the input variables x_0, ..., x_n
 * @returns {number} Value of the evaluation, i.e. f(x)
 */
SymbolicRegression.prototype.evaluate = function (node, inputData) {
    // Get the symbol of the node
    var symbol = node.symbol;
    var value;
    if (symbol == "+") {
        // Add the values of the node's children
        value = this.evaluate(node.children[0], inputData) +
        this.evaluate(node.children[1], inputData);
    } else if (symbol == "*") {
        // Multiply the values of the node's children
        value = this.evaluate(node.children[0], inputData) *
        this.evaluate(node.children[1], inputData);
    } else if (symbol == "-") {
        // Subtract the values of the node's children
        value = this.evaluate(node.children[0], inputData) -
        this.evaluate(node.children[1], inputData);
    } else if (symbol == "/") {
        // Divide the value's of the nodes children. Zero returns a fixed value
        var numerator = this.evaluate(node.children[0], inputData);
        var denominator = this.evaluate(node.children[1], inputData);
        if (denominator != 0) {
            value = numerator / denominator;
        } else {
            value = DIV_BY_ZERO_VALUE;
        }
    } else {
        // Get the variable value
        var split_ = symbol.split(DATA_VARIABLE);
        if (split_.length == 2) {
            var idx = parseInt(split_[1]);
            if (idx > -1 && idx < inputData.length) {
                value = inputData[idx];
            } else {
                throw "Bad index for symbol:" + symbol + ' idx:' + idx;
            }
        // Get the constant value
        } else {
            value = Number(symbol);
        }
    }
    return value;
};

/**
 * Genetic Programming evolutionary search heuristic.
 *
 * Parameters are:
 * - Population size -- Size of the population
 * - Solution size -- Max size of the nodes which represents an individual
 * solution
 * - Max depth -- Max depth of a tree, this is a function of the solution size
 * - Generations -- Number of iterations of the search loop
 * - Elite size -- Number of individuals preserved between generations
 * - Crossover probability -- Probability of crossing over two solutions
 * - Mutation probability -- Probability of mutating a solution
 * - Fitness function -- Method used to evaluate fitness
 * - Symbols -- The symbols that a GP tree can be built from
 *
 * @param {object} params GP parameters
 * @constructor
 */
var GP = function (params) {
    this.params = params;
};

/**
 * Ramped half-half initialization. The individuals in the
 * population are initialized using the grow or the full method for
 * each depth value (ramped) up to max_depth.
 *
 * @returns {Array} Population of individual solutions
 */
GP.prototype.initializePopulation = function () {
    var population = [];
    var populationSize = this.params["population_size"];
    var symbols = this.params["symbols"];
    for (var i = 0; i < populationSize; i++) {
        // Pick full or grow method
        var full = RNG.getRandomBoolean();
        // Ramp up the depth
        var maxDepth = (i % this.params.maxDepth) + 1;
        // Get root symbol
        var symbol = symbols.getRandomSymbol(1, maxDepth, full);
        var root = new TreeNode(null, symbol);
        var tree = new Tree(root);
        // Grow tree if the roo is a function symbol
        if (maxDepth > 0 && contains(symbols["functions"], root.symbol)) {
            root.grow(root, 1, maxDepth, full, symbols);
        }
        population.push(new Individual(tree));
        console.log(i, population[i].genome.root.strAsTree());
        console.log(population[i]);
    }
    return population;
};

/**
 * Compare two individuals based on fitness. Maximizes
 *
 * @param {Individual} individual0 Individual 0
 * @param {Individual} individual1 1ndividual 1
 * @returns {number} 1 if Individual 0 less than Individual 1
 */
function compareIndividuals(individual0, individual1) {
    if (individual0.fitness < individual1.fitness) {
        return 1;
    } else {
        if (individual0.fitness > individual1.fitness) {
            return -1;
        } else {
            return 0;
        }
    }
}

/**
 * Return the best individual from the evolutionary search
 * loop. Starting from an initial population.
 *
 * @param {array} population Initial population
 * @returns {Individual} Best individual solution (model)
 */
GP.prototype.searchLoop = function (population) {
    // Evaluate fitness of initial population
    var population = this.evaluateFitness(population);
    var generation = 0;
    var elites = [];
    while (generation < this.params["generations"]) {
        // Get elite solutions, i.e. the top solutions
        population.sort(compareIndividuals);
        for (var i = 0; i < this.params["elite_size"]; i++) {
            elites.push(population[i]);
        }
        // Select a new population
        var newPopulation = this.tournamentSelection(this.params['tournament_size'],
            population);
        // Crossover the individual solutions in the new population
        newPopulation = this.crossover(this.params['crossover_probability'],
            newPopulation);
        // Mutate the individual solutions in the new population
        newPopulation = this.mutation(this.params['mutation_probability'],
            newPopulation, this.params['maxDepth'], this.params["symbols"]);

        // Evaluate the new population
        newPopulation = this.evaluateFitness(newPopulation);

        // Replace the population with the new population
        population = newPopulation;
        // Add elites to the population
        for (var i = 0; i < this.params["elite_size"]; i++) {
            population[population.length - 1 - i] = elites.pop();
        }
        // Print stats and get best solution
        var bestSolution = this.printStats(generation, newPopulation);
        // Bread if close enough to optimal solution
        if (Math.abs(bestSolution["fitness"]) < this.params["error_cutoff"]) {
            break;
        }
        // Increase the generation
        generation = generation + 1;
    }
    return bestSolution;
};

/**
 * Evaluate the population according to the fitness function.
 *
 * @param {array} population List of individual solutions
 * @returns {array} Evaluated population
 */
GP.prototype.evaluateFitness = function (population) {
   population = this.params["fitnessFunction"].evaluatePopulation(population);
    return population;
};

/**
 * Return individuals from a population by drawing
 * `tournamentSize` competitors randomly and selecting the best
 * of the competitors. `population_size` number of tournaments are
 * held.
 *
 * @param {number} tournamentSize Number of competitors in each tournament
 * @param {array} population List of individuals
 * @returns {Array} List of winners from each tournament
 */
GP.prototype.tournamentSelection = function (tournamentSize, population) {
    var new_population = [];
    // Iterate until there are enough tournament winners selected
    while (new_population.length < population.length) {
        var competitors = [];
        // Randomly select competitors for the tournament
        for (var i = 0; i < tournamentSize; i++) {
            var idx = RNG.getRandomInt(0, population.length - 1);
            competitors.push(population[idx]);
        }
        // Sort the competitors by fitness
        competitors.sort(compareIndividuals);
        // Push the best competitor to the new population
        var winner = competitors[0].copy();
        new_population.push(winner);
    }
    return new_population;
};

/**
 * Print the statistics for the generation and population.
 *
 * @param {number} generation Generation number
 * @param {array} population List of individuals
 * @returns {Individual} Best solution of the generation
 */
GP.prototype.printStats = function (generation, population) {

    /**
     * Get average and standard deviation
     * @param {array} values Values to calculate on
     * @returns {array} Average and standard deviation
     */
    function getAverageAndStd(values) {
        function sum(array) {
            return array.reduce(function (previous_value, current_value) {
                return previous_value + current_value;
            });
        }

        var ave = sum(values) / values.length;
        var std = 0;
        for (var val in values) {
            std = std + Math.pow((val - ave), 2);
        }
        std = Math.sqrt(std / values.length);
        return [ave, std];
    }

    var fitnessValues = [];
    var sizes = [];
    var depths = [];
    for (var i = 0; i < population.length; i++) {
        fitnessValues.push(population[i]["fitness"]);
        population[i]["genome"].calculateDepth();
        sizes.push(population[i]["genome"].nodeCnt);
        depths.push(population[i]["genome"].depth);
    }
    var aveAndStdFitness = getAverageAndStd(fitnessValues);
    var aveAndStdSize = getAverageAndStd(sizes);
    var aveAndStdDepth = getAverageAndStd(depths);

    // Sort population to get best solution
    population.sort(compareIndividuals);
    var bestSolution = population[0];

    console.log("Gen:" + generation + " fit_ave:" + aveAndStdFitness[0] + "+-" + aveAndStdFitness[1] +
    " size_ave:" + aveAndStdSize[0] + "+-" + aveAndStdSize[1] +
    " depth_ave:" + aveAndStdDepth[0] + "+-" + aveAndStdDepth[1] +
    " " + bestSolution["fitness"] + " " + bestSolution["genome"].root.strAsTree());
    console.log("min_fit:" + min(fitnessValues) + " max_fit:" + max(fitnessValues) +
    " min_size:" + min(sizes) + " max_size:" + max(sizes) +
    " min_depth:" + min(depths) + " max_depth:" + max(depths));

    return bestSolution;
};

/**
 * Mutate an individual by randomly picking a node and growing a
 * new subtree from it. Loops over the entire population.
 *
 * @param {number} mutationProbability Probability to mutate an individual
 * @param {array} individuals List of individuals
 * @param {number} maxDepth Max depth of individual
 * @param {Symbols} symbols Symbols used to label nodes with
 * @returns {Array} List of mutated individuals
 */
GP.prototype.mutation = function (mutationProbability, individuals, maxDepth, symbols) {
    var newIndividuals = [];
    for (var i = 0; i < individuals.length; i = i + 1) {
        // Copy the individual
        var newIndividual = individuals[i].copy();
        // Check if the individual will be mutated
        if (RNG.getRandom() < mutationProbability) {
            var nodes = [];
            // Get the nodes and their depths
            newIndividual.genome.depthFirst(newIndividual.genome.root, 0, nodes);
            // Pick a node
            var node_ = RNG.getRandomChoice(nodes);
            var nodeDepth = node_.depth;
            var node = node_.node;
            // Reset the children of the picked node
            node.children = [];
            // Grow tree fully or randomly
            var full = RNG.getRandomBoolean();
            // Set a new symbol for the node
            node.symbol = symbols.getRandomSymbol(nodeDepth, maxDepth, full);
            // Grow subtree according to depth of node
            node.grow(node, nodeDepth + 1, maxDepth, full, symbols);
       }
        newIndividuals.push(newIndividual);
    }
    return newIndividuals;
};

/**
 * The individuals are created by
 * selecting two random nodes from two parents and swapping the
 * subtrees. Checks that the nodes are compatible for crossover, to prevent too
 * large trees. Loops over the entire population.
 *
 * @param {number} crossoverProbability Probability to crossover individuals
 * @param {array} population List of individuals
 * @returns {Array} List of crossed over individuals
 */
GP.prototype.crossover = function (crossoverProbability, population) {
    var CHILDREN = 2;
    var newPopulation = [];

    /**
     * Check compatibility of nodes. Prevent individual solutions to grow too deep
     * @param {array} xoNodes Information regarding the crossover points
     * @param {number} j Index for picked nodes
     * @param {boolean} compatibleNodes If nodes are compatible
     * @returns {boolean} If nodes are compatible
     */
    function checkCompatibility(xoNodes, j, compatibleNodes, maxDepth) {
        // Only pick internal nodes
        compatibleNodes = xoNodes[j].node.children.length > 0 && compatibleNodes;
        if (j > 0) {
            // Only pick nodes of same arity
            compatibleNodes = xoNodes[j].node.children.length == xoNodes[j - 1].node.children.length && compatibleNodes;
            // Only pick nodes if the tree does not grow too deep
            compatibleNodes = xoNodes[j].subTreeSize + xoNodes[j - 1].nodeDepth < maxDepth && compatibleNodes;
            compatibleNodes = xoNodes[j - 1].subTreeSize + xoNodes[j].nodeDepth < maxDepth && compatibleNodes;
        }
        return compatibleNodes;
    }

    for (var i = 0; i < population.length; i = i + CHILDREN) {
        var children = [];
        for (var j = 0; j < CHILDREN; j++) {
            // Pick parents
            var idx = RNG.getRandomInt(0, population.length - 1);
            // Copy child to be identical to parent
            var child = population[idx].copy();
            children.push(child);
        }
        // Check it parents will be crossed over
        if (RNG.getRandom() < crossoverProbability) {
            var xoNodes = [];
            // Keep track of node compatibility
            var compatibleNodes = true;
            for (var j = 0; j < children.length; j++) {
                var nodes = [];
                // Get the nodes and their depths
                children[j]["genome"].depthFirst(children[j]["genome"].root, 0, nodes);
                // Pick a node
                var node_ = RNG.getRandomChoice(nodes);
                var nodeDepth = node_.depth;
                var node = node_.node;
                // Get depth of the subtree
                var subTree = new Tree(node);
                subTree.calculateDepth();
                // Store info about the picked node
                xoNodes.push({node: node, subTreeSize: subTree.depth, nodeDepth: nodeDepth});
                // Check if nodes are compatible for crossover
                compatibleNodes = checkCompatibility(xoNodes, j, compatibleNodes, this.params.maxDepth);
            }
            // Swap subtrees for the children
            if (compatibleNodes) {
                var tmpChildren = xoNodes[0].node.children;
                xoNodes[0].node.children = xoNodes[1].node.children;
                xoNodes[1].node.children = tmpChildren;
            }
        }
        // Add children to population
        for (var j = 0; j < children.length; j++) {
            newPopulation.push(children[j]);
        }
    }
    return newPopulation;
};

/**
 * Check if array contains object.
 *
 * @param {array} array Array to check
 * @param {object} obj Object to check for
 * @returns {boolean} If array contains object
 */
function contains(array, obj) {
    for (var i = 0; i < array.length; i++) {
        if (array[i] === obj) {
            return true;
        }
    }
    return false;
}

/**
 * Random number generator with a specified seed.
 *
 * @type {{seed: number}}
 */
var RNG = {seed: 711};

// From http://indiegamr.com/generate-repeatable-random-numbers-in-js/
// the initial seed
/**
 * Get random number based on seed, [min, max].
 *
 * @param {number} min Minimum number
 * @param {number} max Max number
 * @returns {number} Next random number
 */
RNG.seededRandom = function (min, max) {
    RNG.seed = (RNG.seed * 9301 + 49297) % 233280;
    var rnd = RNG.seed / 233280;
    var value = min + rnd * (max - min);
    return value;
};

/**
 * Get next random number [0, 1]
 *
 * @returns {number} Next random number
 */
RNG.getRandom = function () {
    return RNG.seededRandom(0, 1);
};

/**
 * Get random integer number based on seed, [min, max].
 *
 * @param {number} min Minimum number
 * @param {number} max Max number
 * @returns {number} Next random number
 */
RNG.getRandomInt = function (min, max) {
    var value = RNG.seededRandom(min, max);
    var intValue = Math.floor(value);
    return intValue;
};

/**
 * Get next random boolean [true, false]
 *
 * @returns {number} Next random boolean
 */
RNG.getRandomBoolean = function () {
    return RNG.seededRandom(0, 1) < 0.5;
};

/**
 * Get next random element from an array.
 *
 * @param {array} array Array to pick from
 * @returns {object} Element in array
 */
RNG.getRandomChoice = function (array) {
    var idx = RNG.getRandomInt(0, array.length);
    return array[idx];
};

/**
 * Max value from an array.
 *
 * @param {array} list List of elements
 * @returns {object} Max value
 */
function max(list) {
    return list.reduce(function(previous, current) {
        return previous > current ? previous : current;
    });
}

/**
 * Min value from an array.
 *
 * @param {array} list List of elements
 * @returns {object} Min value
 */
function min(list) {
    return list.reduce(function(previous, current) {
        return previous < current ? previous : current;
    });
}

/**
 * Model quality measures. Returns R^2 and MSE
 * TODO very specific, make more general
 *
 * @param {array} predictionData Data predicted
 * @param {array} observed Data observed
 * @returns {{MSE: number, R_2: number}}
 */
var qualityMeasures = function (predictionData, observed) {
    // Get the predicted value
    var predictions = [];
    for (var i = 0; i < predictionData.length; i++) {
        predictions.push(predictionData[i][predictionData[i].length - 1])
    }
    // Get mean of the observed data
    var mean = observed.reduce(function (pV, cV) {
            return parseFloat(pV) + parseFloat(cV);
        }, 0) / observed.length;
    // Calculate sum of squares, total, regression and residuals
    var ss_tot = 0;
    var ss_reg = 0;
    var ss_res = 0;
    for (var i = 0; i < observed.length; i++) {
        ss_tot += Math.pow(observed[i] - mean, 2);
        ss_reg += Math.pow(predictions[i] - mean, 2);
        ss_res += Math.pow(predictions[i] - observed[i], 2);
    }
    var MSE = ss_res / observed.length;
    var R_2 = 1 - ss_res / ss_tot;
    console.log(MSE);
    console.log(R_2);

    return {MSE: MSE, R_2: R_2};
};

/**
 * Symbols and their arities
 * @type {object}
 */
var arities = {
    1: 0,
    0: 0,
    x_0: 0,
    "+": 2,
    "*": 2,
    "-": 2,
    "/": 2
};

var DIV_BY_ZERO_VALUE = 10000000;
var DEFAULT_FITNESS = -1000000;
var DATA_VARIABLE = "x_";

/**
 * GP default parameters
 * @type {{population_size: number, maxDepth: number, generations: number, mutation_probability: number, tournament_size: number, crossover_probability: number, elite_size: number, error_cutoff: number, symbols: Symbols}}
 */
var gpParams = {
    population_size: 40,
    maxDepth: 3,
    generations: 2,
    mutation_probability: 1.0,
    tournament_size: 2,
    crossover_probability: 1.0,
    elite_size: 1,
    error_cutoff: 0.01,
    symbols: new Symbols(arities)
};


