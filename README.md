# BikiniRepair

BikiniRepair is a prototype system that generates patches and validate them in production without tests..
This code is research code, released under the GPL licence.

[Production-Driven Patch Generation and Validation](https://hal.archives-ouvertes.fr/hal-01370709/document) (Thomas Durieux, Youssef Hamadi and Martin Monperrus), Technical report hal-01370709, HAL, 2016.
```
@techreport{durieux2016production,
 title = {{Production-Driven Patch Generation and Validation}},
 author = {Durieux, Thomas and Hamadi, Youssef and Monperrus, Martin},
 year = {2016},
 institution = {{HAL}},
 number = {hal-01370709},
}```
## Getting Started


### Run the evaluation

1. Gets the Shadow Dataset: https://github.com/Spirals-Team/shadow-dataset
2. Builds each docker image on your system
3. Start BikiniRepair `java -cp target/classes:target/test-classes fr.inria.lille.spirals.bikinirepair.Evaluation` 
4. BikiniRepair is now available at http://localhost:8080
5. Use the python script in the Dataset to reproduce the failure
