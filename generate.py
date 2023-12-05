# generate random input for the knapsack problem
# with n first lines are the weights of the items
# and the n last lines are the values of the items

import random


def generate(num_items, max_weight, max_value):
    weights = [random.randint(1, max_weight) for i in range(num_items)]
    values = [random.randint(1, max_value) for _ in range(num_items)]
    return weights, values


def write_to_file(weights, values, filename):
    with open(filename, 'w') as file:
        file.write('\n'.join(map(str, weights)) + '\n')
        file.write('\n'.join(map(str, values)) + '\n')


num_items = 100
max_weight = 1000
max_value = 1000000
weights, values = generate(num_items, max_weight, max_value)

write_to_file(weights, values, "small.txt")

num_items = 1000
max_weight = 1000
max_value = 1000000
weights, values = generate(num_items, max_weight, max_value)

write_to_file(weights, values, "medium.txt")

num_items = 10000
max_weight = 1000
max_value = 1000000
weights, values = generate(num_items, max_weight, max_value)

write_to_file(weights, values, "large.txt")
