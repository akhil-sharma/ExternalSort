# Duplicate Elimination using The Two-Pass Multiway Merge Sort

Using `jdk-11.0.11.9-hotspot`

## Folder Structure
- `src`: The folder to maintain sources.
- `output`: The sorted relation with distinct records is stored in this folder.
- `input`: Program looks for the specified relation in this folder.

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

## Execution Parameters

The execution parameters such as the available memeory size and command line arguments can be modified in the `.vscode/launch.json`.

## Running the code

There are two primary ways in which we can run this program.

    1. Simply use the `run java` button in vscode. This method will run the program according to the configurations specified in `.vscode/launch.json`.

    2. Manual compilation of the src.
        > javac -d ./bin  src/*.java
        This will save the generated bin files in the `bin` directory.

        > java -classpath bin App fileSmall.txt
        Here, `fileSmall.txt` contains the input relation and is located in the `input` directory.

<b>Note</b>: All the input files are expected to be in the input directory. This behavior may later be changed to allow input files from any location.

## Command Line Arguments

1. `filename`: The program expects the first command line argument to be the name of file containing the relation. This file must be located in the `input` directory. 

2. `v`: Verbose. Optional argument to display all the duplicated records as they are found.