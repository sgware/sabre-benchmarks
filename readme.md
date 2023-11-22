# Sabre Benchmark Tool

This is a tool for testing many configurations of the
[Sabre Narrative Planner](https://github.com/sgware/sabre) on a variety of
[benchmark story planning problems](problems) collected from several authors. It
also serves as a repository of example Sabre problems.

This tool automatically produces a summary of the performance of every planner
on every problem in [HTML](results.html) and [plain text](results.txt) formats.
It also writes the contents of each table in the report to [CSV format](results)
for easy use by other tools.

Many of these problems were originally designed for other narrative planners
with different syntax and features. A [technical report](report.pdf) explains
the origins of each problem and the methods used to translate them into Sabre
problems. When possible, the [original versions](originals) of these problems
are also included in this repository for reference.

## Usage

To clone this project (including Sabre as a submodule), compile the code, and
run it:

```
git clone --recurse-submodules https://github.com/sgware/sabre-benchmarks
cd sabre-benchmarks
javac -cp sabre/lib/sabre.jar -sourcepath src -d bin src/edu/uky/cs/nil/sabre/bench/Main.java
java -Xms60g -Xmx60g -cp bin;sabre/lib/sabre.jar edu.uky.cs.nil.sabre.bench.Main
```

The `-Xms60g` argument sets the Java Virtual Machine's minimum heap space to 60
gigabytes, and the `-Xmx60g` argument sets the JVM's maximum heap space to 10
gigabytes. You can adjust these numbers up or down depending on how much memory
is available and how many threads will run simultaneously.

All relevant settings can be found at the top of
[`Main.java`](src/edu/uky/cs/nil/sabre/bench/Main.java). You can change how many
threads run in parallel. You can set the maximum number of nodes visited, nodes
generated, and time spent by each search. You can change the number of times
each planner is run on each problem and whether the order of actions is shuffled
between runs. You can comment out benchmark problems or planner configurations
you don't want to test.

To add a new benchmark problem, you need to place the relevant Sabre problem
file in the [problems](problems) directory and add a new line to
[`Main.java`](src/edu/uky/cs/nil/sabre/bench/Main.java) that gives a unique name
for the benchmark problem, the name of the problem file, the goal utility that
must be achieved, and the author temporal limit, character temporal limit, and
epistemic limit on the search. Optionally, you can also add a known solution to
the [solutions](solutions) directory. The name of the file should match the name
of the benchmark problem (not the name of the problem file). Before the tests
begin, every planner will attempt to reproduce that solution using a special
heuristic that only allows the planner to use actions from that solution in its
search.

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
benchmark problems.

## Version History

- Version 1.0 - First public release testing 27 versions of 14 problems using
  Sabre version 0.7.

## Contributions

This project is maintained by Stehen G. Ware, Associate Professor of Computer
Science at the [University of Kentucky](http://uky.edu) and director of the
[Narrative Intelligence Lab](http://cs.uky.edu/~sgware). He can be reached at
sgware@gmail.com, but he is forever busy and makes no promises about the
timeline of accepting pull requests.

To have your benchmark problem included in this collection, please create a pull
request with the following changes:
- Add your problem file to the [problems](problems) directory.
- Your problem file must include a block comment at the top of the file
  explaining the story it models, the origin of the problem, and at least one
  known solution. See the [Raiders of the Lost Ark](problems/raiders.txt)
  problem for an example.
- Add one or more lines to
  [`Main.java`](src/edu/uky/cs/nil/sabre/bench/Main.java) with relevant versions
  of the problem to be tested and their recommended search limits. Problems
  should be listed in chronological order based on when the original version of
  the problem was created. New problems should be added to the end. Problems
  that are translations of existing narrative planning problems created for
  other systems should be inserted based on the date the original version of the
  problem was created for the original system.
- If your problem is a translation of some existing problem, please add the
  original problem to the [originals](originals) directory.
- Add a description of your problem to the [technical report](report/report.tex)
  explaining this collection. Problems should be added in chronological order
  based on the creation of the original problem, as above. You should also add
  your name to the list of authors and a preferred citation for your problem to
  the [bibliography file](report/bibliography.bib).
- If possible, rerun the full suite of tests to update the results. This may take
  several days, and is not required, but if the maintainers have to rerun the
  tests it will delay the time it takes to merge your problem into the main
  branch.

## Citation

The [technical report](report.pdf) explaining the origin of each benchmark
problem and the recommended settings for testing them can be cited as:

> Stephen G. Ware and Rachelyn Farrell, "A Collection of Benchmark Problems for
> the Sabre Narrative Planner," Technical Report, Narrative Intelligence Lab,
> University of Kentucky, November 2023.

BibTeX entry:

```
@techreport{ware2023collection,
  author={Ware, Stephen G. and Farrell, Rachelyn},
  title={A Collection of Benchmark Problems for the {Sabre} Narrative Planner},
  institution={Narrative Intelligence Lab},
  address={University of Kentucky},
  year={2023},
  month={November},
  howpublished={\url{https://github.com/sgware/sabre-benchmarks/blob/main/report.pdf}}
}
```