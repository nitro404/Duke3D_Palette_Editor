Duke Nukem 3D Palette Editor 1.0.1

Release Date: May 28, 2014
Development Time: 4 Days
Developer: Kevin Scroggins
E-Mail: nitro404@gmail.com
Website: http://www.nitro404.com

==============
 REQUIREMENTS
==============
 + Java Runtime Environment (Version 1.6 or newer)

==========
 FEATURES
==========
  + Java-based multi-platform code.
  + Simple and easy to use GUI with tabbed editing.
  + Support for reading, writing importing & exporting DAT / PAL / ACT / BMP
    / PNG / GIF palette files!
  + Support for importing / exporting between different file formats (including
    LOOKUP.DAT sub-palettes)!
  + Easy file opening with drag-and-drop support!
  + Full dynamic plugin support via jar files for handling custom file types!
  + Version checker.

=============
 DESCRIPTION
=============
  So I recently tried to find a half decent tool to work with the Duke 3D
  PALETTE.DAT and LOOKUP.DAT files so I could edit the palette swaps. I was
  sadly disappointed with how non-functional and downright awful the few
  programs I could find were, so I did a bit of research and made my own
  palette editor to deal with those pesky files! Probably a little overdue, as
  I doubt many people really bother modding Duke Nukem 3D anymore.. but hey,
  if anyone else gets any use out of it, all the power to you! I went above
  and beyond adding features to it and making it easy to use, so hopefully it
  doesn't go unappreciated!
  
  Check out the sections below for additional information.
  
  If you happen to have any complaints, suggestions, bugs, comments or
  contributions, please feel free to submit an issue or pull request on the
  GitHub page: https://github.com/nitro404/duke3d_palette_editor
 
  Thank you!

============
 HOW TO USE
============
  First, make sure that you properly have the Java 1.6 Runtime Environment
  installed and properly configured. You can start the program by opening the
  jar package "Duke Nukem 3D Palette Editor.jar" - this is a pre-packaged Java
  executable. You will initially be presented with the console tab - this is
  simply a location for information and debug output to be placed for review,
  you can generally disregard it.
  
  Next, you can begin work by either opening an existing palette file (ie.
  LOOKUP.DAT or PALETTE.DAT) or creating a new one (excluding DAT files) and
  going from there. Alternatively, you can also take advantage of the
  click-and-drag support, and simply drag the files you wish to open directly
  onto the program window. Each 16x16 (256) colour palette will be displayed as
  a grid of clickable buttons. Each button represents a pixel, and you can
  change its colour by clicking on it and picking a new colour with the colour
  chooser. Keep in mind that colours for DAT files are scaled up / down 4
  times, so this means a value of 256 will actually be 64 when it is written to
  the DAT file, so plan your colour choices accordingly.
  
  If you wish, you can also export any palette (or sub-palette) to a different
  format so you can edit it in a different program, like Adobe Photoshop, Gimp
  or Paint.NET if you prefer. If the palette being exported has a number of
  sub-palettes, you can export each one individually - a prompt will be
  displayed to allow you to do as such.
  
  When you are done with your external editing (or if you have another palette
  file you would like to import into your current one), you can import other
  palettes into your palette using the import feature. Should be fairly
  straight forward.
  
  When you wish to save your changes, you can do so by simply selecting save,
  or if you wish to save as a different file type (essentially the same
  functionality as exporting), you can select save as.
  
  All changes are logged, so if you attempt to close the program with any
  unsaved changes, it will display a series of prompts to make sure you don't
  lose your work!
  
  There is also lots of options that are changeable using the menu at the top
  of the window. Don't like the size of the pixel buttons? Don't like being
  notified about new updates? You can change it!
  
  Lastly, there is a full plugin system for handling the different palette
  file types. All default file types are handled through plugins, so if you
  don't like the way the default plugins work, you are free to edit, re-design
  and replace them however you like! For more information on how the plugins
  work and how to make your own, check out the compiling and plugins sections.

==========
 BUILDING
==========
  If you wish to re-compile the program using the provided source code, you are
  welcome to do so as long as you don't take credit for my hard work and keep
  it for personal use. If you wish to have a feature or plugin added to the
  master copy, it would be much appreciated if you contacted me directly so
  that we could work something out!
  
  The source code was written using Java 1.6 with Eclipse, and as such all
  Eclipse project files are provided, as well as ANT build files for easy
  compilation and packaging of JAR files. The plugins rely on having a compiled
  "Plugin Library.jar" package and source files at the relative directory
  "../Plugin Library/". If this JAR is not present, the plugin project is not
  linked to the plugin library, the plugin will not compile! The Palette Editor
  itself does not rely on any of these sub projects and can compile
  independently of them.
  
  Compiled plugins, along with their corresponding CFG files must be placed
  inside of the "Plugins" directory. For example, the "Plugins" directory must
  be in the same directory as "Duke Nukem 3D Palette Editor.jar" and within the
  "Plugins" directory, there can be any number of arbitrarily named folders,
  each one representing a plugin. Within each plugin folder, there must be an
  arbitrarily named JAR and CFG file pair. The names do not matter, so long as
  the JAR file is properly linked inside of the CFG file.

========================
 PLUGIN DEFINITION FILE
========================
  As mentioned above, if you wish to experiment or have a file you wish to have
  supported, you are welcome to write your own plugins, or even replace / edit
  / re-design my own! If you design a nice plugin, or find a way to improve on
  what I have, I urge you to contact me and let me know, so that it can
  potentially be added to the master copy (with credit where credit is due, of
  course!)
  
  Plugins contain two parts, a JAR package containing all relevant classes for
  the operation of the plugin and a CFG file defining all required information
  about the plugin itself. You can use the following as a template for your CFG
  file:

Palette Plugin Definition File 1.0

Plugin Version: 1.0
Plugin Type: Palette
Plugin Name: <FILE_TYPE> Palette File
Supported Palette File Types: <FILE_EXTENSION>
Instantiable: <true / false>
Plugin Jar File Name: <FILE_TYPE>.jar
Palette Class Name: palette.Palette<FILE_TYPE>

  To better elaborate on these parts of the plugin definition file, the header
  is the first part that will be read in by the program and is required for the
  definition file to even be read. Currently, the program only supports both
  version 1.0 of the file definition and 1.0 of the plugin version. Note that
  these versions are independent of each other. The only plugin type currently
  supported is type "Palette", meaning a handler for reading / writing
  / accessing palette data (or pixels, if you will). So this will need to be
  specified and left unchanged. Anything specified with <> around it must be
  changed.
  
  More specifically, FILE_TYPE must be replaced with whatever file type this
  plugin is for. For example, this could simply be the extension of the file
  or a more accurate description. The text "Palette File" can be changed as
  well, but it's preferred if you leave it for clarity. Just note that when
  naming your class file, it cannot have spaces or special characters so you
  will need to acommodate that. 
  
  FILE_EXTENSION must be replaced with the extension of the file type that this
  plugin is designed for. Some plugins can support multiple file extensions,
  for example the default "Image" plugin supports a bunch of file extensions,
  so you can specify them in a list separated by commas or semicolons, like
  this: "PNG, GIF, BMP" (without quotes).
  
  Lastly, the instantiable variable must be specified with either true or
  false. To be instantiable, this means that it is possible for a new instance
  of this palette type to be created and populated with some data. This is done
  whenever the user creates a new palette of this type, or exports from another
  palette type to this palette type.

====================
 PLUGIN SOURCE CODE
====================
  To get started on your own plugin (or if you wish to change one of the
  defaults), I would reccomend starting with one of the existing plugins which
  is closest to the file type you wish to replicate, copying the plugin source
  folder and re-naming / changing things as is appropriate. For custom DAT
  files, the DAT plugin would be your best starting point, or for different
  image formats, the "Image" plugin would be best, or lastly if you're dealing
  with binary files, "ACT" or "PAL" would be a good starting point. You'll need
  a custom plugin definition (CFG) file, so check out the above section for
  more information on that. You if you wish to use the ANT build file, you will
  also need to update it with new file / folder names (if appropriate).
  
  It is preferred that you put all of your palette plugin source files inside
  of the "palette" package.
  
  In order to create a custom palette plugin, the default abstract "Palette"
  class must be extended and have all abstract functions defined. You are also
  free to override already defined functions, if it is appropriate, although
  most should be fine left as is.
  
  The intermediary form for pixel / colour data when passing between palette
  formats is an array of Java "Color" objects.
  
  The file which each palette corresponds to on the hard disk is referred to by
  the m_file variable. The palette can only be read from / written to disk if
  this file is set to something.
  
  The m_loaded variable corresponds to whether or not the palette data has been
  loaded into memory (or at least initialized with default data, if a new
  palette was created).
  
  The publicly accessable constants PALETTE_WIDTH and PALETTE_HEIGHT correspond
  to the dimensions of the palette (and currently only 16x16 is supported, so
  they have been hard-coded as such). NUMBER_OF_COLOURS corresponds to the
  total amount of colour slots in the palette, and as such is hard coded to 256
  (or 16x16).
  
  It is highly recommended that you have an overridden constructor which takes
  a File as an argument.
  
  The accessor functions pertaining to file types refer to the actual
  extensions of files which this plugin supports. Usually there would only be
  one, but in the case of plugins like the Image plugin, a number of different
  file extensions are supported, like PNG, GIF and BMP so they must be
  specified internally as a string array and made accessible.
  
  The palette description accessor function corresponds to the name or easy way
  of identifying what a sub palette represents. This is mostly only applicable
  for LOOKUP.DAT which has 5 different sub-palettes. Normally, this will only
  need to return the text "Default" or anything else you feel is appropriate.
  
  The instantiable checker function corresponds to whether it is possible for a
  new instance of this palette type to be created and populated with some data.
  This is done whenever the user creates a new palette of this type, or exports
  from another palette type to this palette type. So be sure to override this
  and return true if this is possible, or else your plugin will not be listed
  when creating a new palette or exporting an existing palette!
  
  The colour accessors function return a Color object for the pixel colour data
  at the corresponding x and y position in the specified sub-palette (if
  appropriate). Most palette plugins will only have one sub-palette, so you can
  just verify that the index is equal to 0 instead.
  
  The colour data accessor functions convert all of the internal palette data
  to a neutral array of Color objects, so that it can be passed around to
  different palette files.
  
  The update pixel functions are for changing the colour of a pixel at the
  specified x and y position in the corresponding sub-palette (if appropriate).
  
  The update colour functions are used for changing the internal contents of
  the palette with a completely different set of colour data. Note that the
  index offset for the local palette as well as the index offset for the data
  being passed in. For example, the colour data array could contain a full set
  of colours from a list of sub-palettes and you only want to take a subset of
  those colours and apply it to a specific local sub-palette. See the default
  plugins if you need some examples for how to do this.
  
  The colour fill functions are for changing every colour in the palette to a
  new colour. This is generally done when the palette is first initialized when
  the user chooses to create a new palette.
  
  The load and save functions are used for reading from / writing to disk. All
  palette data is stored locally, and a palette can only be read if it is not
  already loaded(initialized) and only written if it is already loaded.

===========
 CHANGELOG
===========
  Version 1.0.1 (May 28, 2014)
    + Fixed a minor bug in the DAT plugin with sub-palettes having the wrong
      descriptions.
    + Changed the default config file to "Palette Editor.ini" to prevent
      conflicts with "DUKE3D.CFG" if running the game from the same directory
      as the palette editor.
    + Re-factored a couple class names for clarity.

  Version 1.0 (May 27, 2014)
    + Initial release!
