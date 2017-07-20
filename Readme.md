# Padins

A workflow management system for scientific devops.

*Goal :* to develop a computational tool for science researcher. This tool
let them do three main things :
* Design a scientific worklow
* Execute the workflow on a distributed system
* Visualize the results, including matrices as videos

## Typical Use Cases
* User wants to produce a Boussinesq Simulation on a hillslope and see the results
on a chart, choosing the kind of chart. Then go through the chart.
* User wants to adjust its mathematic model comparing the simulation's results
and the data she picked up on the real case study.

## Getting Started

There are two way to run this project : docker or using the binaries.

### Using docker :
**Prerequisites**

To run this project, you'll need :

*Docker*
```
https://www.docker.com/community-edition
```

**Then you just need to run it, no installation needed**
1.  `docker run -it --rm -p 8080:8080 -v /var/run/docker.sock:/var/run/docker.sock antoinecheronirisa/padins`
2. Go to : http://localhost:8080


### Using the binaries
**Prerequisites**

To run this project, you'll need :

*Docker*
```
https://www.docker.com/community-edition
```

*A Java Virtual Machine*
```
https://java.com/en/download/
OR
apt-get install default-jdk
```

You will also need maven and npm
```
sudo apt install maven
sudo apt-get install npm
```

### Installing
First : open your terminal and go to the folder where you want to install this project

Then download the version you want,
For the stable version (latest release) :
```
wget "https://github.com/AntoineCheron/padins-backend/archive/v0.1.0.zip"
unzip v0.1.0.zip -d ./
cd padins-backend-0.1.0
```

For the last development version :
```
wget "https://github.com/AntoineCheron/padins-backend/archive/master.zip"
unzip master.zip -d ./
cd padins-backend-master
```

Then, just one step to install :
```
./install.sh
```

### Running
1. Go to the folder where you installed the project
2. Run the following command :
```
./run.sh
```
