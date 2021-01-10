# kata-cloud-aws-hello-world

:fr: Sommaire / :gb: Table of Contents
=================

<!--ts-->

- [:fr: Description du projet](#fr-description-du-projet)
- [:gb: Project Description (coming-soon)](#gb-project-description-coming-soon)


<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>


---

# :fr: Description du projet

le but de ce projet est de chercher à faire comme un "kata", mais pour du code et déploiement cloud.

Ici l'objectif est de déployer plusieurs machines EC2 exposant une page html "hello-world", dans un groupe d'auto-scaling, 
et derrière un Load Balancer. 

En essayant d'appliquer une approche proche de tdd autant que possible.
Pour les tests on utilisera Cucumber et le sdk aws java. 
Pour les déploiements nous utiliserons `Terraform`.