# “Plant Computer” – open-source implementation in Java/Kotlin

**Plant Computer** is a new, improved open-source version of a 
computer system for plant replication in a controlled environment. 
The technology was based on an Internet of Things (IoT) platform, 
which includes: an automated system for remote monitoring and 
controlling climate, energy, and plant growth within a specialized, 
isolated growth chamber.

![](images/Figure01.png?raw=true)

## Intro
The remotely monitored and controlled climate variables were: 
- air temperature, 
- humidity, 
- potential of hydrogen (pH), 
- electrical conductivity, 
- root zone temperature, 
- carbon dioxide level, 
- dissolved oxygen in the substrate 

and many other variables that could be digitally monitored and 
controlled within the growth chamber, to obtain different 
plant *phenotype expressions*.

Records of cultivated plants are stored in the form of so-called 
**“climate recipes”** for future replication, and the computer 
system got the popular name *“food computer”* or more specifically *“plant computer”*.

![](images/Figure02.png?raw=true)

## Usage
Whole *plant computer* project and all project modules (*iotapp*)
are designed as **Gradle** project/modules. Therefore, use 
**Gradlew Wrapper** ( *./gradlew* in both Windows and Linux/Mac) 
to clean, build, test and run project/modules.

To run Gradle tasks, from IntelliJ one can use *Terminal window* 
(be in the root path where *gradlew* file is located).
(Instead of Terminal window, an alternative is to install and 
use Gradle Plugin in IntelliJ.)

#### Example:
To run *plant computer* (as desktop app) in real *measurement mode* simply use:
```
./gradlew run
```
or more verbose:
```
./gradlew :iotapp:run --args='--simulationMode=false'
```

![](images/Figure03.png?raw=true)

To run *plant computer* (as desktop app) in *simulation mode* - turn it on:
```
./gradlew :iotapp:run --args='--simulationMode=true'
```
(or alternatively use Gradle plugin and set: run --args='--simulationMode=true').

To run *plant computer* (as web application) in web browser (http://localhost:8080/):
```
./gradlew jproRun
```
![](images/Figure04.png?raw=true)

To deploy *plant computer* (fat jar generated with Spring Boot) on Raspberry Pi:
```
./gradlew deploy
```
then run jar (> java -jar iotapp.jar) on RPi.


## Built With
* [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/) - Integrated development environment
* [OpenJDK 11](https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime) - Open Java development kit
* [OpenJFX](https://openjfx.io/) - Open JavaFX
* [TilesFX](https://github.com/HanSolo/tilesfx) - JavaFX library containing tiles for Dashboards
* [JPro](https://www.jpro.one/) - JavaFX in the Browser library
* [Gradle](https://gradle.org/) - Dependency management & build automation tool
* [Eclipse Paho](https://www.eclipse.org/paho/) - MQTT publish/subscribe client
* [InfluxDb](https://www.influxdata.com/) - InfluxDB time series database


## Testing

To run all available Java test:
```
./gradlew test 
--tests "hr.unipu.A_PlantComputerAppTestJava" 
--tests "hr.unipu.B_UserInterfaceTestJava" 
--tests "hr.unipu.C_RecipeTestJava"
```
or to run all available Kotlin test:
```
./gradlew test 
--tests "hr.unipu.A_PlantComputerAppTest" 
--tests "hr.unipu.B_UserInterfaceTest" 
--tests "hr.unipu.C_RecipeTest"
```


## Authors
- **Siniša Sovilj**<sup>1</sup> <sinisa.sovilj@unipu.hr>
- **Dalibor Fonović**<sup>1</sup> <dalibor.fonovic@unipu.hr>
- **Krešimir Pripužić**<sup>2</sup> <kresimir.pripuzic@fer.hr>
- **Nikola Tanković**<sup>1</sup> <nikola.tankovic@unipu.hr>

<sup>1 = Juraj Dobrila University of Pula, Faculty of Informatics, HR-52100 Pula, CROATIA </sup>  \
<sup>2 = University of Zagreb, Faculty of Electrical Engineering and Computing, HR-10000 Zagreb, CROATIA </sup>


## References
1. Sovilj, S., Fonović, D., Hager, M. and Kovaček, M. (2022),
   "Food Computer" – A Demo Platform for Internet of Things Education.
   *2022 45th Jubilee International Convention on Information, Communication and Electronic Technology (MIPRO)*, 
   pp. 1454-1460, doi: 10.23919/MIPRO55190.2022.9803354.


## License
This project is licensed under the Apache License 2.0 license. See the [LICENSE](LICENSE) file for details.


## Acknowledgments
*Plant Computer* was inspired by discontinued MIT
Open Agriculture Foundation project 
[OpenAg](https://github.com/OpenAgricultureFoundation) 
and the exceptional contribution of students Mislav Hager and Mateo Kovaček.