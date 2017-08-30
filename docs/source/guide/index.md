---
layout: default
id: index
title: What is it?
next: guide/how-it-works.html
parent: guide
---

Padins is a web integrated developement environment for designing and running **scientific simulations** and more generaly any **mathematic application**. Simulations are represented as workflows and composed of customizable components. We have designed Padins as a scalable cloud application that can take advantage of cluster's performance in order to run simulation fastly, but that can also be installed easily on a single pc.

Creating a workflow in Padins is as simple as adding 2 components on the graph : a simulation component that will contain the simulation's code, and a visualization component. Then linking the two components together, also known as **nodes**, adding a few line of code in the simulation node and clicking Run. Results will be available as soon as the execution finishes in the visualisation component. 

For the moment, Padins supports only the Python language as the programming language in the simulations' node. In the future, it would be interested to also support R, Java, C and C++, at least. 

### User Interface

![](../images/padins-ui.png)