# Jadex-DCOP-Solver

Using Active Components (also known as JadeX), a framework for programming distributed concurrent systems, our group has created a program that uses an existing solving algorithm (Adopt) to solve Distributed Constraint Optimisation Problems (DCOP). Our program is modeled off of the popular Python library PyDCOP that uses a set of DCOP solving algorithms in a message based framework. 

Our program uses a connected network of JadeX agents who work together on the same problem concurrently to solve the DCOP. The problem description is uploaded to a single agent using the SnakeYAML library that serialises YAML files into Java objects. Through a system of Host agents the problem is divided and distributed across the JadeX network. Each host then launches solver agents equal to the number of agents required to handle its share of the problem. Each solver agent has its own variable in the DCOP to solve and knows of the solver agents with which it shares constraints. Solvers, however, do not know the details of other agents constraints. 


# Contributers:
This project was completed at Swinburne University of Technology in 2020 by:

- Matthew Jackson
- Angus Maude
- Zac McDonald

Report: https://docs.google.com/document/d/1UkAKT9iV3LL1P_ZZDHAq61tvT3aJzpFyhA6i3zfweO4/edit?usp=sharing
Github: https://github.com/Zac-McDonald/IntelligentSystems-DCOP-Solver <br />
Github (Network): https://github.com/Zac-McDonald/IntelligentSystems-DCOP-Solver/network <br />
Trello: https://trello.com/b/6ovyFlWI/jadex-dcop-solver
