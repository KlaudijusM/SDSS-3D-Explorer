# SDSS Galaxy Explorer
## A Visual Game Interface for exploring the Sloan Digital Sky Survey

The _SDSS Galaxy Explorer_ is a tool that uses data from the vast publicly available _Sloan Digital Sky Survey_ database which contains multi-million records of various astronomical objects and portrays it in a 3-dimensional space. It allows the user to explore, interact and learn about the cosmos while doing so in an easily accessible and interesting way.

### How does it work
This tool uses the [JMonkey](http://jmonkeyengine.org/) game engine to visually portray data from a local _.csv_ file containing the fundamental properties of the chosen galaxies. The data is processed and the galaxies are displayed as points for the user to fly through and select an area of the sky to explore further. When the user decides upon the area of exploration he is taken up close to visually see the nearby galaxies. To display the imaging data for the galaxies a request is being sent to the _SDSS_ database to receive an image.

### What is the SDSS
>_The Sloan Digital Sky Survey has created the most detailed three-dimensional maps of the Universe ever made, with deep multi-colour images of one third of the sky, and spectra for more than three million astronomical objects._

Find out more at the [SDSS Website](http://www.sdss.org/)

### Why is a data file needed
Even though the data is available online, different users might have different needs. Some user might want to explore the universe that contain objects of specific properties others might not care and would like to see all available objects. Some machines might not be able to process and render large number of objects thus requiring a smaller amount of objects. All of this weights up to the better approach of reading data of a local file.

### Why is an active Internet connection needed
Due to different user needs of how many objects they require to see it is more practical to retrieve images when needed rather than storing them locally which would take up a vast amount of space

### Where can I get a data file
There is a data file included with this project *(galaxy_list.csv)* that holds the data for the first 100,000 galaxies from the _SDSS_ photometric data table. Advanced users can use  [CasJobs](http://skyserver.sdss.org/casjobs/) to retrieve specific objects they want this tool to process and place it in the root directory of the main _.jar_ file.

### Releases
This tool is currently still in its early stage of development thus no releases are present. Users who wish to use the tool while it is in development can clone this project and should have no problems compiling with the _JMonkey_ IDE.

### Features/Development & Releases
Here you can track the development as well as the releases of the project.

Current progress:

  - [x] Data is being read from file
  - [x] Galaxies are correctly positioned on screen
  - [x] Shader implementation
  - [x] Users can explore the basic view of the Universe
  - [x] Sample images are created in place of points
  - [ ] More properties of the universe are used and displayed on screen
  - [ ] **First Release**
  - [ ] Images are retrieved from the database and shown on screen as galaxies
  - [ ] **Second Release**
  - [ ] GUI development
  - [ ] Detailed object information on request
  - [ ] Saving/Loading of interesting positions/objects
  - [ ] **Third Release**
