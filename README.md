# Itzal

Itzal is a prototype system that generates patches and validate them in production directly.
This code is research code, released under the GPL licence. It was formerly called BikiniRepair.

Short paper: [Production-Driven Patch Generation](https://hal.archives-ouvertes.fr/hal-01463689/document) (Thomas Durieux, Youssef Hamadi and Martin Monperrus), In Proceedings of the 37th International Conference on Software Engineering (ICSE), track on New Ideas and Emerging Results, 2017.

```
@inproceedings{durieux2016production,
 title = {{Production-Driven Patch Generation}},
 author = {Durieux, Thomas and Hamadi, Youssef and Monperrus, Martin},
 booktitle = {{Proceedings of the 37th International Conference on Software Engineering (ICSE), track on New Ideas and Emerging Results}},
 url = {https://hal.archives-ouvertes.fr/hal-01463689/document},
 year = {2017},
}
```



Long paper: [Production-Driven Patch Generation and Validation](http://arxiv.org/pdf/1609.06848) (Thomas Durieux, Youssef Hamadi and Martin Monperrus), Technical report hal-01370709, HAL, 2016.

```
@techreport{durieux2016production,
 title = {{Production-Driven Patch Generation and Validation}},
 author = {Durieux, Thomas and Hamadi, Youssef and Monperrus, Martin},
 year = {2016},
 institution = {{HAL}},
 number = {hal-01370709},
}
```

## Getting Started


### Run the evaluation

1. Gets the benchmark: https://github.com/Spirals-Team/itzal-experiments
2. Builds each docker image on your system
3. Start Itzal/BikiniRepair `java -cp target/classes:target/test-classes fr.inria.lille.spirals.bikinirepair.Evaluation` 
4. The dashboard is now available at http://localhost:8080
5. Use the python script in the Dataset to reproduce the failure
