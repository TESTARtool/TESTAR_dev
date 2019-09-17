#! /usr/env python

# The MIT License (MIT)

# Copyright (c) 2013, 2014 Erik Hemberg

# Permission is hereby granted, free of charge, to any person
# obtaining a copy of this software and associated documentation files
# (the "Software"), to deal in the Software without restriction,
# including without limitation the rights to use, copy, modify, merge,
# publish, distribute, sublicense, and/or sell copies of the Software,
# and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:

# The above copyright notice and this permission notice shall be
# included in all copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
# EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
# NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
# BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
# ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
import csv

import random
import math
import copy
import argparse

"""
Genetic Programming
===================

Implementation of GP to describe how the algorithm works. For teaching purposes.

Fitness Function
----------------

Find a symbolic expression (function) which yields the lowest error for a given
set of inputs.

Inputs have explanatory variables that have a corresponding output. The input
data is split into test and training data. The training data is used to
generate symbolic expressions and the test data is used to evaluate the
out-of-sample performance of the evaluated expressions.

.. codeauthor:: Erik Hemberg <hembergerik@csail.mit.edu>

"""

class Tree(object):
    """
    A Tree has a root which is an object of class TreeNode

    Attributes:

    - Root -- The root node of the tree
    - Node count -- The number of nodes in the tree
    - Depth -- The maximum depth of the tree
    """

    def __init__(self, root):
        """
        Constructor

        :param root: Root node of the tree
        :type root: TreeNode
        """
        # Root of tree
        self.root = root
        # Number of nodes in the tree
        self.node_cnt = 1
        # Largest depth of the tree
        self.depth = 1

    def grow(self, node, depth, max_depth, full, _symbols):
        """
        Recursively grow a node to max depth in a pre-order, i.e. depth-first
        left-to-right traversal.

        :param node: Root node of subtree
        :type node: TreeNode
        :param depth: Current tree depth
        :type depth: int
        :param max_depth: Maximum tree depth
        :type max_depth: int
        :param full: grows the tree to max depth when true
        :type full: bool
        :param _symbols: set of symbols to chose from
        :type _symbols: Symbols
        """

        # grow is called recursively in the loop. The loop iterates arity number
        # of times. The arity is given by the node symbol
        for _ in range(_symbols.arities[node.symbol]):
            # Get a random symbol
            symbol = _symbols.get_rnd_symbol(depth, max_depth, full)
            # Increase the node count
            self.node_cnt += 1
            # Create a child node object of the current node
            child = TreeNode(node, symbol)
            # Append the child node to the current node
            node.children.append(child)
            # Call grow with the child node as the current node
            self.grow(child, depth + 1, max_depth, full, _symbols)

    def calculate_depth(self):
        """
        Return the maximum depth of the tree.

        :returns: Maximum depth of the tree
        :rtype: int
        """

        # Get a list of all nodes
        all_nodes = self.depth_first(self.root)
        # Find the depth of each node
        # TODO improve depth calculation by not iterating over all nodes. Let
        # get_depth or depth_first have side-effects
        node_depths = [self.get_depth(node) for node in all_nodes]
        # The maximum depth of the tree is the node with the greatest depth
        self.depth = max(node_depths)

        return self.depth

    def depth_first(self, root):
        """
        Return a list of nodes of recursively collected by pre-order
        depth-first
        left-to-right traversal.

        :param root: Start of traversal
        :type root: TreeNode
        :return: list of nodes in pre-order
        :rtype: list
        """

        # Add the root node to the list of nodes
        nodes = [root]
        # Iterate over the children of the root node. If the node is a
        # leaf node then it has no children and there will be no more
        # calls to depth_first.
        for child in root.children:
            # Append the nodes returned from the child node
            nodes += (self.depth_first(child))

        # Return the list of nodes
        return nodes

    @staticmethod
    def get_depth(node):
        """
        Return depth of node by counting the number of parents when
        traversing up the tree.

        :param node: Node which depth is calculated
        :type node: TreeNode
        :return: depth of node in tree
        :rtype: int
        """

        # Set the starting depth
        depth = 0
        # If the current node has a parent then set the parent to be
        # the current node
        while node.parent:
            # The parent of the current node is set to be the current
            # node
            node = node.parent
            # Depth is increased by one
            depth += 1

        # Return the depth after following all the parent references
        return depth

    def __str__(self):
        """
        Return string representation of tree

        :return: string of tree
        :rtype: str
        """
        # String representation
        _str = 'node_cnt:%d depth:%d root:%s' % \
               (self.node_cnt, self.depth, self.root.str_as_tree())
        # Return string representation
        return _str

    
class TreeNode(object):
    """
    A node in a tree.

    Attributes:
      - Parent -- The parent of the node. None indicates no parent, i.e. root
      node
      - Symbol -- The label of the node
      - Children -- The children of the node

    """

    def __init__(self, parent=None, symbol=None):
        """
        Constructor

        :param parent: Parent node
        :type parent: TreeNode
        :param symbol: Node symbol
        :type symbol: str
        """
        # The parent of the tree node
        self.parent = parent
        # The symbol of the node (a.k.a label)
        self.symbol = symbol
        # The children of the node
        self.children = []

    def str_as_tree(self):
        """
        Return an s-expression for the node and its descendants.

        :return: S-expression
        :rtype: str
        """

        # The number of children determines if it is a internal or
        # leaf node
        if len(self.children):
            # Append a ( before the symbol to denote the start of a subtree
            _str = "(" + str(self.symbol)
            # Iterate over the children
            for child in self.children:
                # Append a " " between the child symbols
                _str += " " + child.str_as_tree()

            # Append a ) to close the subtree
            _str += ")"

            # Return the subtree string
            return _str
        else:
            # Return the symbol
            return str(self.symbol)

class Symbols(object):
    """
    Symbols are functions (internal nodes) or terminals (leaves)

    Attributes:
      - Arities -- Dictionary with the symbol as a key and the arity of symbol
      the symbol as the value
      - Terminals -- List of the terminal symbols, arity is 0
      - Functions -- List of the function symbols, arity greater than 0

    """

    def __init__(self, arities):
        """
        Constructor

        :param arities: symbol names and their arities
        :type arities: dict
        """

        # Arities dictionary with symbol as key and arity as value
        self.arities = arities
        # List of terminal symbols
        self.terminals = []
        # List of function symbols
        self.functions = []

        # Append symbols to terminals or functions by looping over the
        # arities items
        for key, value in self.arities.items():
            # A symbol with arity 0 is a terminal
            if value == 0:
                # Append the symbols to the terminals list
                self.terminals.append(key)
            else:
                # Append the symbols to the functions list
                self.functions.append(key)

    def get_rnd_symbol(self, depth, max_depth, full=False):
        """
        Get a random symbol. The depth determines if a terminal
        must be chosen. If full is specified a function will be chosen
        until the max depth.

        :param depth: current depth
        :type depth: int
        :param max_depth: maximum allowed depth
        :type max_depth: int
        :param full: grow to full depth
        :type full: bool
        :return: symbol
        :rtype: str
        """

        # Pick a terminal if max depth has been reached
        if depth >= max_depth:
            # Pick a random terminal
            symbol = random.choice(self.terminals)
        else:
            # Can it be a terminal before the max depth is reached
            # then there is 50% chance that it is a terminal
            if not full and bool(random.getrandbits(1)):
                # Pick a random terminal
                symbol = random.choice(self.terminals)
            else:
                # Pick a random function
                symbol = random.choice(self.functions)

        # Return the picked symbol
        return symbol


class Individual(object):
    """
    A GP Individual.

    Attributes:
      - Genome -- A tree
      - Fitness -- The fitness value of the individual

    DEFAULT_FITNESS
      Default fitness value of an unevaluated individual

    """

    DEFAULT_FITNESS = -1000

    def __init__(self, genome):
        """
        Constructor

        :param genome: genome of the individual
        :type genome: Tree
        """
        # Set the genome (a.k.a input) of the individual
        self.genome = genome
        # Set the fitness to the default value
        self.fitness = Individual.DEFAULT_FITNESS

    def __lt__(self, other):
        """
        Returns the comparison of fitness values between two individuals.

        :param other: other individual to compare against
        :type other: Individual
        :returns: if the fitness is lower than the other individual
        :rtype: bool
        """
        # Compare the fitness of this and the other individual
        return self.fitness < other.fitness

    def __str__(self):
        """
        Returns a string representation of fitness and genome

        :returns: String with fitness and genome
        :rtype: bool
        """
        # String representation by calling the root node of the genome
        # as a s-expression
        _str = 'Individual: %f, %s' % \
               (float(self.fitness), self.genome.root.str_as_tree())
        # Return string representation
        return _str


class SymbolicRegression(object):
    """
    Evaluate fitness based on fitness cases and target values. Fitness cases are
    a set of exemplars (input and output points) by comparing the error between
    the output of an individual(symbolic expression) and the target values.

    Attributes:
    
    - Fitness cases -- Input values for the exemplars
    - Targets -- The target values corresponding to the fitness case
    - Variables -- The current value of the variables in the evaluated exemplar

    """

    def __init__(self, fitness_cases, targets):
        """ Constructor

        :param fitness_cases: Exemplar values
        :type fitness_cases: list
        :param targets: Value corresponding to the fitness cases
        :type targets: list
        """
        #Matrix where each is row a case and each column is a variable
        self.fitness_cases = fitness_cases
        #Each row is the response to the corresponding fitness cases
        self.targets = targets

        assert len(self.fitness_cases) == len(self.targets)

    def __call__(self, individual):
        """Evaluates and sets the fitness in an individual. Fitness is the
        negative mean square error(MSE).

        :param individual: Individual solution to evaluate
        :type individual: Individual
        """
        # Initial fitness value
        fitness = 0.0
        # Calculate the error between the output of the individual solution and
        # the target for each input
        for case, target in zip(self.fitness_cases, self.targets):
            # Set the variables to be accessible for the evaluation function
            self.variables = case
            # Get output from evaluation function
            output = self.evaluate(individual.genome.root)
            # Get the squared error
            error = output - target
            fitness += error*error

        # Get the mean fitness and assign it to the individual
        individual.fitness = -fitness/float(len(self.targets))

    def evaluate(self, node):        
        """Evaluate a node recursively. The node's symbol is evaluated.

        :param node: Evaluated node
        :type node: TreeNode
        :returns: Value of the evaluation
        :rtype: float
        """
        
        #Identify the node symbol
        if node.symbol == "+":
            # Add the values of the node's children
            return self.evaluate(node.children[0]) +\
                    self.evaluate(node.children[1])
        elif node.symbol == "-":
            # Subtract the values of the node's children
            return self.evaluate(node.children[0]) -\
                    self.evaluate(node.children[1])
        elif node.symbol == "*":
            # Multiply the values of the node's children
            return self.evaluate(node.children[0]) *\
                    self.evaluate(node.children[1])
        elif node.symbol == "/":
            # Divide the value's of the nodes children. Too low values of the
            # denominator returns the numerator
            numerator = self.evaluate(node.children[0])
            denominator = self.evaluate(node.children[1])
            if abs(denominator) < 0.00001:
                denominator = 1

            return numerator / denominator
        elif node.symbol.startswith("x"):
            # Get the variable value
            return self.variables[int(node.symbol[1:])]
        else:
            #The symbol is a constant
            return float(node.symbol)


class GP(object):
    """
    Genetic Programming implementation.

    Attributes:

    - Population size -- Size of the population
    - Solution size -- Max size of the nodes which represents an individual
      solution
    - Max depth -- Max depth of a tree, this is a function of the solution size
    - Generations -- Number of iterations of the search loop
    - Elite size -- Number of individuals preserved between generations
    - Crossover probability -- Probability of crossing over two solutions
    - Mutation probability -- Probability of mutating a solution
    - Fitness function -- Method used to evaluate fitness
    - Symbols -- The symbols that a GP tree can be built from

    POPULATION_FILE
      File where population is saved

    """

    POPULATION_FILE = 'gp_population.dat'

    def __init__(self, population_size, max_size, generations,
                 elite_size, crossover_probability,
                 mutation_probability, fitness_function, symbols):
        """Constructor
        
        :param population_size: Size of population
        :type population_size: int
        :param max_size: Bitstring size for an individual solution
        :type max_size: int
        :param generations: Number of iterations of the search loop
        :type generations: int
        :param elite_size: Number of individuals preserved between generations
        :type elite_size: int
        :param crossover_probability: Probability of crossing over two solutions
        :type crossover_probability: float
        :param mutation_probability: Probability of mutating a solution
        :type mutation_probability: float
        :param fitness_function: Method used to evaluate fitness of a solution
        :type fitness_function: Object
        :param symbols: Symbols used to build trees
        :type symbols: Symbols
        """
        # Population size is the number of individual solutions
        self.population_size = population_size
        # Size of the individual solution
        self.max_size = max_size
        # Number of iterations of the search loop
        self.generations = generations
        # Number of individual solutions that are preserved between
        # generations
        self.elite_size = elite_size
        # Probability of crossover
        self.crossover_probability = crossover_probability
        # Probability of mutation
        self.mutation_probability = mutation_probability
        # Function that is used to evaluate the fitness of the
        # individual solution
        self.fitness_function = fitness_function
        # Max depth is a function of the max_size
        self.max_depth = GP.get_max_depth(self.max_size)
        # The symbols used in the GP Trees
        self.symbols = symbols

    @classmethod
    def get_max_depth(cls, size):
        """
        Return the max depth of a binary tree given a size. The size is
        the number of nodes.

        :param size: Number of tree nodes
        :type size: int
        :returns: Max depth of the binary tree
        :rtype: int
        """
        return int(math.log(size, 2))

    def initialize_population(self):
        """
        Ramped half-half initialization. The individuals in the
        population are initialized using the grow or the full method for
        each depth value (ramped) up to max_depth.

        :returns: List of individuals
        :rtype: list
        """

        individuals = []
        for i in range(self.population_size):
            #Pick full or grow method
            full = bool(random.getrandbits(1))
            #Ramp the depth
            max_depth = (i % self.max_depth) + 1
            #Create root node
            symbol = self.symbols.get_rnd_symbol(1, max_depth)
            root = TreeNode(None, symbol)
            tree = Tree(root)
            #Grow the tree if the root is a function symbol
            if tree.depth < max_depth and symbol in self.symbols.functions:
                tree.grow(tree.root, 1, max_depth, full, self.symbols)
            individuals.append(Individual(tree))
            print('Initial tree %d: %s' % (i, tree.root.str_as_tree()))

        return individuals

    @classmethod
    def evaluate_fitness(cls, individuals, fitness_function):
        """
        Perform the fitness evaluation for each individual.

        :param individuals: Population to evaluate
        :type individuals: list
        :param fitness_function: Fitness function to evaluate the population on
        :type fitness_function: Object
        """

        # Iterate over all the individual solutions
        for ind in individuals:
            # Execute the builtin '__call__' method of the fitness function
            fitness_function(ind)

    def search_loop(self, population):
        """
        Return the best individual from the evolutionary search
        loop. Starting from the initial population.
        
        :param population: Initial population of individuals
        :type population: list
        :returns: Best individual
        :rtype: Individual
        """

        # Evaluate fitness
        self.evaluate_fitness(population, self.fitness_function)
        best_ever = None

        #Generation loop
        generation = 0
        while generation < self.generations:
            new_population = []
            # Selection
            parents = self.tournament_selection(population)

            # Crossover
            while len(new_population) < self.population_size:
                # Vary the population by crossover
                new_population.extend(
                    # Pick 2 parents and pass them into crossover.
                    self.subtree_crossover(*random.sample(parents, 2))
                )
            # Select population size individuals. Handles uneven population
            # sizes, since crossover returns 2 offspring
            new_population = new_population[:self.population_size]

            # Vary the population by mutation
            new_population = list(map(self.subtree_mutation, new_population))

            # Evaluate fitness
            self.evaluate_fitness(new_population, self.fitness_function)

            # Replace population
            population = self.generational_replacement(new_population,
                                                       population)
            # Print the stats of the population
            self.print_stats(generation, population)

            # Set best solution
            population.sort(reverse=True)
            best_ever = population[0]

            # Increase the generation counter
            generation += 1

        return best_ever

    def print_stats(self, generation, individuals):
        """
        Print the statistics for the generation and population.
       
        :param generation:generation number
        :type generation: int
        :param individuals: population to get statistics for
        :type individuals: list
        """

        def get_ave_and_std(values):
            """
            Return average and standard deviation.            

            :param values: Values to calculate on
            :type values: list
            :returns: Average and Standard deviation of the input values
            :rtype: tuple
            """
            _ave = float(sum(values)) / len(values)
            _std = math.sqrt(float(
                sum((value - _ave) ** 2 for value in values)) / len(values))
            return _ave, _std

        # Make sure individuals are sorted
        individuals.sort(reverse=True)
        # Get the fitness values
        fitness_values = [i.fitness for i in individuals]
        # Get the number of nodes
        size_values = [i.genome.node_cnt for i in individuals]
        # Get the max depth
        depth_values = [i.genome.calculate_depth() for i in individuals]
        # Get average and standard deviation of fitness
        ave_fit, std_fit = get_ave_and_std(fitness_values)
        # Get average and standard deviation of size
        ave_size, std_size = get_ave_and_std(size_values)
        # Get average and standard deviation of max depth
        ave_depth, std_depth = get_ave_and_std(depth_values)
        # Print the statistics
        print(
            "Gen:%d evals:%d fit_ave:%.2f+-%.3f size_ave:%.2f+-%.3f "
            "depth_ave:%.2f+-%.3f %s" %
            (generation, (self.population_size * generation),
             ave_fit, std_fit,
             ave_size, std_size,
             ave_depth, std_depth,
             individuals[0]))

    def subtree_mutation(self, individual):
        """
        Return a new individual by randomly picking a node and growing a
        new subtree from it.
        
        :param individual: Individual to mutate
        :type individual: Individual
        :returns: Mutated individual
        :rtype: Individual
        """

        new_individual = Individual(copy.deepcopy(individual.genome))
        # Check if mutation should be applied
        if random.random() < self.mutation_probability:
            # Pick node
            node = random.choice(
                new_individual.genome.depth_first(new_individual.genome.root))
            # Clear children
            node.children[:] = []
            # Get depth of the picked node
            node_depth = new_individual.genome.get_depth(node)
            # Set a new symbol for the picked node
            node.symbol = self.symbols.get_rnd_symbol(node_depth,
                                                      self.max_depth)
            # Grow tree if it was a function symbol
            if node.symbol in self.symbols.functions:
                # Grow subtree
                new_individual.genome.grow(node, node_depth, self.max_depth,
                                           bool(random.getrandbits(1)),
                                           self.symbols)

            # Set the new node count in the tree
            node_cnt = len(
                new_individual.genome.depth_first(new_individual.genome.root))
            new_individual.genome.node_cnt = node_cnt
            # Get the new max depth of the tree
            new_individual.genome.calculate_depth()

        # Return the individual
        return new_individual

    def subtree_crossover(self, parent1, parent2):
        """
        Returns two individuals. The individuals are created by
        selecting two random nodes from the parents and swapping the
        subtrees.

        :param parent1: Individual to crossover
        :type parent1: Individual
        :param parent2: Individual to crossover
        :type parent2: Individual
        :returns: Two new individuals
        :rtype: tuple
        """

        # Copy the parents to make offsprings
        offsprings = (Individual(copy.deepcopy(parent1.genome)),
                      Individual(copy.deepcopy(parent2.genome)))

        # Check if offspring will be crossed over
        if random.random() < self.crossover_probability:
            #Pick a crossover point
            offspring_0_node = random.choice(
                offsprings[0].genome.depth_first(offsprings[0].genome.root))
            #Only crossover internal nodes, not only leaves
            if offspring_0_node.symbol in self.symbols.functions:
                # Get the nodes from the second offspring
                nodes = offsprings[1].genome.depth_first(
                    offsprings[1].genome.root)
                # List to store possible crossover nodes
                possible_nodes = []
                #Find possible crossover points
                for node in nodes:
                    # If there is a matching arity the nodes can be crossed over
                    matching_type = self.symbols.arities[node.symbol] == \
                                    self.symbols.arities[
                                        offspring_0_node.symbol]
                    # Append the node to the possible crossover nodes
                    if matching_type:
                        possible_nodes.append(node)

                # Pick a crossover point in the second offspring
                if possible_nodes:
                    #Pick the second crossover point
                    offspring_1_node = random.choice(possible_nodes)
                    #Swap the children of the nodes
                    node_children = (
                        offspring_0_node.children, offspring_1_node.children)
                    # Copy the children from the subtree of the first offspring
                    # to the chosen node of the second offspring
                    offspring_1_node.children = copy.deepcopy(node_children[0])
                    # Copy the children from the subtree of the second offspring
                    # to the chosen node of the first offspring
                    offspring_0_node.children = copy.deepcopy(node_children[1])

        # Return the offsprings
        return offsprings

    def tournament_selection(self, population, tournament_size=2):
        """
        Return individuals from a population by drawing
        `tournament_size` competitors randomly and selecting the best
        of the competitors. `population_size` number of tournaments are
        held.

        :param population: Population to select from
        :type population: list
        :param tournament_size: Size of a tournament
        :type tournament_size: int
        :returns: selected individuals
        :rtype: list
        """

        # Iterate until there are enough tournament winners selected
        winners = []
        while len(winners) < self.population_size:
            # Randomly select tournament size individual solutions
            # from the population.
            competitors = random.sample(population, tournament_size)
            # Rank the selected solutions
            competitors.sort(reverse=True)
            # Append the best solution to the winners
            winners.append(competitors[0])

        return winners

    def generational_replacement(self, new_population, old_population):
        """
        Return new a population. The `elite_size` best old_population
        are appended to the new population. They are kept in the new
        population if they are better than the worst.

        :param new_population: the new population
        :type new_population: list
        :param old_population: the old population
        :type old_population: list
        :returns: the new population with the best from the old population
        :rtype: list
        """

        # Sort the population
        old_population.sort(reverse=True)
        # Append a copy of the best solutions of the old population to
        # the new population. ELITE_SIZE are taken
        for ind in old_population[:self.elite_size]:
            new_population.append(copy.deepcopy(ind))

        # Sort the new population
        new_population.sort(reverse=True)

        # Set the new population size
        return new_population[:self.population_size]

    def run(self):
        """
        Return the best solution. Create an initial
        population. Perform an evolutionary search.

        :returns: Best solution
        :rtype: Individual
        """

        #Create population
        population = self.initialize_population()
        # Start evolutionary search
        best_ever = self.search_loop(population)

        return best_ever


def parse_exemplars(file_name):
    """
    Parse a CSV file. Parse the fitness case and split the data into
    Test and train data. In the fitness case file each row is an exemplar
    and each dimension is in a column. The last column is the target value of
    the exemplar.

    :param file_name: CSV file with header
    :type file_name: str
    :return: Fitness cases and targets
    :rtype: list
    """

    # Open file
    in_file = open(file_name, 'r')
    # Create a CSV file reader
    reader = csv.reader(in_file, delimiter=',')

    # Read the header
    headers = reader.next()
    print("Reading: %s headers: %s" % (file_name, headers))

    # Store fitness cases and their target values
    fitness_cases = []
    targets = []
    for row in reader:
        # Parse the columns to floats and append to fitness cases
        fitness_cases.append(map(float, row[:-1]))
        # The last column is the target
        targets.append(float(row[-1]))

    in_file.close()

    return fitness_cases, targets


def get_arities():
    """
    Return a symbol object. Helper method to keep the code clean.

    :return: Symbols used for GP individuals
    :rtype: Symbols
    """
    # Dictionary of symbols and their arity
    arities = {"1": 0,
               "x0": 0,
               "x1": 0,
               "+": 2,
               "-": 2,
               "*": 2,
               "/": 2,
               }
    # Create a symbols object
    symbols = Symbols(arities)
    return symbols


def get_test_and_train_data(fitness_cases_file, test_train_split):
    """
    Return test and train data.

    :param fitness_cases_file: CSV file with a header.
    :type fitness_cases_file: str
    :param test_train_split: Percentage of exemplar data used for training
    :type test_train_split: float
    :return: Test and train data. Both cases and targets
    :rtype: tuple
    """

    fitness_cases, targets = parse_exemplars(fitness_cases_file)
    # TODO get random cases instead of according to index
    split_idx = int(math.floor(len(fitness_cases) * test_train_split))
    training_cases = fitness_cases[:split_idx]
    test_cases = fitness_cases[split_idx:]
    training_targets = targets[:split_idx]
    test_targets = targets[split_idx:]
    return (test_cases, test_targets), (training_cases, training_targets)


def main():
    """Search. Evaluate best solution on out-of-sample data"""
    
    # Command line arguments
    parser = argparse.ArgumentParser()
    # Population size
    parser.add_argument("-p", "--psize", type=int, default=20,
                        help="population size")
    # Size of an individual
    parser.add_argument("-m", "--maxsize", type=int, default=14,
                        help="individual size")
    # Number of elites, i.e. the top solution from the old population
    # transferred to the new population
    parser.add_argument("-e", "--esize", type=int, default=0, help="elite size")
    # Generations is the number of times the EA will iterate the search loop
    parser.add_argument("-g", "--generations", type=int, default=10,
                        help="number of generations")
    # Random seed. Used to allow replication of runs of the EA. The search is
    # stochastic and and replication of the results can be guaranteed by using
    # the same random seed
    parser.add_argument("-s", "--seed", type=int, default=0,
                        help="seed number")
    # Probability of crossover
    parser.add_argument("-cp", "--crossover", type=float, default=0.1,
                        help="crossover probability")
    # Probability of mutation
    parser.add_argument("-mp", "--mutation", type=float, default=0.8,
                        help="mutation probability")
    # Fitness case file
    parser.add_argument("-fc", "--fitness_cases", default="",
                        help="fitness cases file")
    # Test-training data split
    parser.add_argument("-tts", "--test_train_split", type=float, default=0.7,
                        help="test-train data split")
    # Parse the command line arguments
    args = parser.parse_args()

    # Set arguments

    population_size = args.psize
    max_size = args.maxsize
    generations = args.generations
    elite_size = args.esize
    seed = args.seed
    crossover_probability = args.crossover
    mutation_probability = args.mutation
    test_train_split = args.test_train_split
    fitness_cases_file = 'fitness_cases.csv'  # args.fitness_cases

    test, train = get_test_and_train_data(fitness_cases_file, test_train_split)

    symbols = get_arities()

    # Print EA settings
    print(args, symbols.arities)

    # Set random seed if not 0 is passed in as the seed
    if seed != 0:
        random.seed(seed)

    fitness_function = SymbolicRegression(train[0], train[1])
    gp = GP(population_size, max_size,
            generations, elite_size, crossover_probability,
            mutation_probability, fitness_function, symbols)

    best_ever = gp.run()
    print("Best train:" + str(best_ever))
    #Test on out-of-sample data
    out_of_sample_test(best_ever, test[0], test[1])


def out_of_sample_test(individual, fitness_cases, targets):
    """
    Out-of-sample test on an individual solution

    :param individual: Solution to test on data
    :type individual: Individual
    :param fitness_cases: Input data used for testing
    :type fitness_cases: list
    :param targets: Target values of data
    :type targets: list
    """
    fitness_function = SymbolicRegression(fitness_cases, targets)
    fitness_function(individual)
    print("Best test:" + str(individual))

if __name__ == '__main__':
    main()
