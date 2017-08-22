const JavadocToMarkdown = require('./javadoc-to-markdown.js');
const FileSystem = fs = require('fs');
const Path = require('path');
const JavaDoc2MD = new JavadocToMarkdown();

const PATH_TO_MD_DIRECTORY = './source/backend-api/'; // Must end with a /
const PATH_TO_YML_DIRECTORY = './source/_data/';
/**
 * Returns a list of the files included in the given directory.
 *
 * Copyright @mnpenner from GitHub : https://gist.github.com/kethinov/6658166
 *
 * @param dir {String} the path to the directory, can be relative
 * @returns {Array} the list of files included in the given directory
 */
function readDirR(dir) {
    return FileSystem.statSync(dir).isDirectory()
        ? Array.prototype.concat(...FileSystem.readdirSync(dir).map(f => readDirR(Path.join(dir, f))))
        : dir;
}

/**
 * Generate the documentation for the Java files in the given directory
 * @param dir {String} the path to the java sources. Can be absolute or relative
 * @param pathToMDDir {String} path where to store md files. Can be absolute or relative
 * @param pathToYMLDir {String} path where to store the yml file. Can be absolute or relative
 */
function generateDoc(dir, pathToMDDir, pathToYMLDir, headingsLevel) {
    // Use the readDirR function in order to retrieve the list of files that are in src/main/java
    const javaSources = readDirR(dir);

    // Convert the javadoc to markdown and write a .md file for each code file
    createAndStoreJavaDocToMDFiles(javaSources, pathToMDDir, headingsLevel);

    // Generate the menu file for the website's sidebar
    generateWebsiteMenu(javaSources, dir, pathToMDDir, pathToYMLDir);

    console.log('[SUCCESS] Mardown documentation successfully created !');
}

/**
 * Convert JavaDoc into Markdown files and store them in the given destination directory.
 * @param javaFiles {Array} the list of java files' path
 * @param destination {String} the destination dir
 * @param headingsLevel {number} the headings level to use as the base (1-6)
 */
function createAndStoreJavaDocToMDFiles (javaFiles, destination, headingsLevel) {
    javaFiles.forEach( function(file) {
        // Read the file
        var code = fs.readFileSync(file, 'utf-8');
        // Convert it to markdown
        var doc = JavaDoc2MD.fromJavadoc(code, headingsLevel);
        // Write the markdown file
        var filename = file.substring(file.lastIndexOf('/') + 1, file.lastIndexOf('.')) + '.md';
        var fileContent = generateMDFileHeader(filename) + doc;
        fs.writeFile(destination + filename, fileContent, 'utf-8', function (err) {
            if(err) throw err;
        });
    });
}

/**
 * Generate the markdown file header with a few variables that are used by the website to choose the proper template
 * and sidebar.
 *
 * @param filename
 * @returns {string}
 */
function generateMDFileHeader (filename) {
    return '---\nlayout: default\nid: ' + filename.replace(/.md/g, '') + '\ntitle: Backend API\nparent: backend-api\n---\n';
}

/**
 * Generate the yml file read to generate the sidebar links on the doc website, and store it.
 *
 * @param javaFiles {Array} the list of java files' path
 * @param srcDir {String} the path to the java sources. Can be absolute or relative
 * @param pathToMDDir {String} path where to store md files. Can be absolute or relative
 * @param pathToYMLDir {String} path where to store the yml file. Can be absolute or relative
 */
function generateWebsiteMenu (javaFiles, srcDir, pathToMDDir, pathToYMLDir) {
    const menu = {
        name: pathToMDDir.substring(pathToMDDir.substring(0, pathToMDDir.length - 1).lastIndexOf('/') +1, pathToMDDir.length - 1) + '.yml',
        content: ''
    };

    const files = [];
    // Remove the srcDirfrom the name of the file
    javaFiles.forEach(function(file) {
        files.push(file.replace(srcDir, ''));
    });

    // Retrieve the list of first order packages and associate each file to its package
    const packages = orderFilesPerPackages(files);

    // Build the yml file content
    menu.content += '- title: Introduction\n';
    menu.content += '  items: \n';
    menu.content += '  - id: index\n';
    menu.content += '    title: Index\n';

    for(var key in packages) {
        // skip loop if the property is from prototype
        if(!packages.hasOwnProperty(key)) continue;

        var pack = packages[key];

        menu.content += '- title: ' + pack.name + '\n';
        menu.content += '  items: \n';
        pack.files.forEach(function(file) {
            // Add the id formatted in snake_case
            menu.content += '  - id: ' + file.replace(/.java/g, '') + '\n';
            menu.content += '    title: ' + file + '\n';
        });
    }

    // Write the yml file
    fs.writeFileSync(pathToYMLDir + menu.name, menu.content, 'utf-8');
}

/**
 * Returns an object containing the given files ordered by 1st order package.
 * @param files {String[]} files' path to order
 * @returns {Object} the given files ordered by 1st order package
 */
function orderFilesPerPackages (files) {
    const packages = {};

    files.forEach(function(file) {

        var package = file.substring(0, file.indexOf('/'));
        if (packages.hasOwnProperty(package)) {
            packages[package].files.push(file.substring(file.lastIndexOf('/') + 1, file.length));
        } else {
            packages[package] = {
                name: package,
                files: [file.substring(file.lastIndexOf('/') + 1, file.length)]
            };
        }
    });

    return packages;
}


// Run the script
generateDoc('../src/main/java/fr/irisa/diverse/', PATH_TO_MD_DIRECTORY, PATH_TO_YML_DIRECTORY, 1);