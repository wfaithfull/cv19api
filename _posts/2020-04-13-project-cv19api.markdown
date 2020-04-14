---
layout: post
title:  "cv19api"
date:   2020-04-14 15:00:00 +0000
---
* [cv19api on GitHub](https://github.com/lbandc/cv19api)
* [Colab notebook demonstrating it's use](https://colab.research.google.com/drive/1SFKsljWqc2IR5xV1vBzdRu6HFr-Kr1OK#scrollTo=3Ue5Yjg-EppX)

Hello.

We are the Lancaster Beer & Code collective. A group of technologically-inclined friends who appreciate the finer things in life - beer and code. Unfortunately the pubs are closed right now.

* [Will Faithfull](https://www.linkedin.com/in/will-faithfull/)
* [Jon Hill](https://www.linkedin.com/in/jon-hill-604764/)
* [Ryan Callihan](https://www.linkedin.com/in/ryan-callihan/)
* [Jonathan Smillie](https://www.linkedin.com/in/jsmillie/)

We've been busy over the easter weekend. I (Will) noticed this [reddit post](https://www.reddit.com/r/ukpolitics/comments/fykikm/nhs_england_data_broken_down_for_deaths_by_day/) on Friday, and thought it was fascinating. I hadn't thought much about it, but if the COVID-19 death totals being announced weren't actually deaths that occurred on those days, how different are the true figures?

And also, why do NHS England publish everything on Excel spreadsheets, which are bloody hard work to aggregate.

With those problems in mind, we set upon a solution. [A Spring Boot Application](https://github.com/lbandc/cv19api) which automatically scrapes these excel spreadsheets daily, ingests the data into a database, and exposes it as a queryable api for [data science applications](https://colab.research.google.com/drive/1SFKsljWqc2IR5xV1vBzdRu6HFr-Kr1OK#scrollTo=3Ue5Yjg-EppX).

We built this for our own curiosity, but we provide it free of use to anyone and everyone. We will try and keep it up to date as long as this crisis continues.