# Sabre Benchmarks

About

https://github.com/sgware/sabre

## Usage

To clone this project (including Sabre as a submodule), compile the code, and
run the code, run this series of commands in a terminal:

```
git clone --recurse-submodules https://github.com/sgware/sabre-benchmarks
cd sabre-benchmarks
javac -cp sabre/lib/sabre.jar -sourcepath src -d bin src/edu/uky/cs/nil/sabre/bench/Main.java
java -Xms10g -Xmx10g -cp bin;sabre/lib/sabre.jar edu.uky.cs.nil.sabre.bench.Main
```

On Max/Linux:

```
java
```

## Ownership and License

The Sabre Benchmark Tool was developed by Stephen G. Ware PhD, Associate
Professor of Computer Science at the University of Kentucky.

Sabre and the code for this Benchmark Tool are released under the Creative
Commons Attribution-NonCommercial 4.0 International license. This means you are
free to share, remix, and add to this software for non-commercial projects as
long as you give credit to the original creators. See the license file for
details. The University of Kentucky retains all right not specifically granted.

To license this code for a commercial project, contact the University of
Kentucky Office of Technology Commercialization at <otcinfo@uky.edu>.

The benchmark problems included with this tool were created by various authors
who hold the copyrights. See each problem file for authorship details.

Special thanks to Rachelyn Farrell for her help compiling and translating
benchark problems.