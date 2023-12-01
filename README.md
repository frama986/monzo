# Web Crawler

Starting from an initial URL, this web crawler will fetch and print all the links it can find in a webpage. 
Then it will enqueue and process the URLs not already visited and with the same domain as the initial one.

## Architecture

We have three main components:
 - A Web Crawler runtime that is taking care of interfacing with the user for the initial url, spinning up the engine and waiting for the process to finish
 - A Web Crawler Engine that is taking care of enqueuing the new URLs to be processed, printing the results and keeping the status of the entire process
 - A Worker that is taking care of fetching the data from the URL, parsing the page and extracting the links

![Web Crawler Architecture](./docs/webcrawler-arch.png)

## Setup and run the service
This service requires Java 21 to run.

NOTE: if you don't want to install Java you can build it and run it using Docker, see [below](#run-the-application-using-docker).

### Install Java 21
If you don't have Java already on your machine you can easily install it using [SDKMAN!](https://sdkman.io/). \
First, install SDKMAN! ([here](https://sdkman.io/install)) running the following command:
```shell
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
```
Then install Java using the Env command ([here](https://sdkman.io/usage))
```shell
sdk env install
# you can simply do "sdk env" after the first installation
```

### Run the application
A makefile has been added to simplify this process.

Open a terminal and 
To build and run the application simply do
```shell
make
# or make run
```
If for any reasons it should not work correctly, you can try
```shell
make jar
```

### Run the application using Docker
A Docker file is provided to build and run the application in case you don't want to install Java.

Open a terminal in the root directory of the project and run the following commands:
```shell
docker build -t webcrawler .
docker run -it --rm webcrawler
```

## Usage and behaviour
Once started the application will ask for an initial url.\
If you don't specify any it will default to `https://www.google.com`.\
If the protocol is not specified it will use `https` as default.\
If you type `exit` it will exit.

The application will print the url of each inspected page, followed by the list of all the href it has found in the page.\
This includes any type of reference, like `mailto:` ones.\
Then it will inspect only the urls it has not visited yet with the same domain of the initial url.\
Once the processing is completed it will ask for a new url.

## Notes and possible improvements
First of all I used Java because it is the language I'm more comfortable with and I thought it would be a good use case
for using the Virtual threads, hence why I used Java 21. Go could have been another good solution with goroutines and channels,
but I don't have enough experience with it.

Everything is persisted in memory, and it works because it runs in a single instance.
For a distributed implementation we should probably think of using some external persistence to store the information like
the visited URLs.

To avoid concurrency issues when processing the result of the parsing I used `synchronized` on the method.
This is creating a bit of a bottleneck and there is the chance that the worker threads are waiting to acquire the lock.
This could be mitigated by using another ExecutorService and enqueuing the results there and having more granular locks.

There are no metrics or monitoring, for a real production application it would be useful to add them.

The HTTPClient uses the same threads of the ExecutorService (sync requests).
Here it could be interesting to use two different pools and tune better the concurrency.
Some websites could throttle the requests, so we need to be careful on how many requests we send to the same domain.

Error handling can be improved with a more granular and robust logic.
For example, it could be nice to introduce a sort of retry in case of errors while fetching a web page.
Also, if an exception is not handled correctly, the main loop could get stuck waiting for the engine to finish processing.

While parsing the page the application is taking any href it can find. It would be nice here to have a better logic to
extract only some sort of types of urls.
Urls are not "cleaned", so identical urls with different request parameters are considered as different urls,
and this can lead to longer processing.

All the configuration is hardcoded, and it would be nice to move it in a property file or environment variable,
for example the number of concurrent threads.