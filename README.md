# graal-stratified-negation
A tool to compute and visualize the Graph of Rules Dependencies of an ontology composed of existential rules with default negation. It can compute a stratification if the rule base is stratifiable and apply forward chaining on a given fact base.

# How to Use
## Command Line
Classical usage :

```java -jar graal-stratified-negation.jar [options]```

List of supported options : 
- `-f`, `--input-file`
  Rule set input file.
  Default: -
- `-r`, `--rule-set`
  Print the rule set.
  Default: false
- `-g`, `--grd`
  Print the Graph of Rule Dependencies.
  Default: false
- `-s`, `--print-scc`
  Print the Strongly Connected Components.
  Default: false
- `-G`, `--print-gscc`
  Print the graph of the GRD Strongly Connected Components.
  Default: false
- `-c`, `--forward-chaining`
  Apply forward chaining on the specified Fact Base.
  Default: -
- `-h`, `--help`
  Print this message.
  Default: false
  `-v`, `--version`
  Print version information
  Default: false
- `-w`, `--window`
  Launch the GUI.
  Default: false
                
## Graphical User Interface
To launch the GUI use :

```java -jar graal-stratified-negation.jar -w```

A user manual is available in `doc/manual.pdf`. The Graphical User Interface provides the same functionalities as the command line usage, even more.
- Some colors :
    * Green : the green color (on a rule or a strongly connected component) means that everything is OK
    * Red : the red color means that there is a problem and the Ontology can not be stratified
        - On a rule : the rule belongs to a circuit wich contains a negative reliance
        - On a strongly connected component : the strongly connected component is a circuit wich contains a negative reliance
-While displaying the Graph of Strongly Connected Components, clicking on a strongly connected component will open a new window with the Graph Of Rule Dependencies associated to it.
- You can export an ontology in DLGP format
- You can export a saturated fact base in DLGP format
- You can export both the Graph of Rule Dependencies and the Graph of the Strongly Connected Components

:warning: Due to theoritical limitations, computation of the dependencies between rules may take some time on big ontologies.

# Future Features
In order to work with bigger ontologies this tool needs some improvments :
- [ ] Import Rules from a database
- [ ] Import Facts from a database
- [ ] Export saturated set of facts to a database