#target photoshop

// Get the active document
var doc = app.activeDocument;

// Store the number of layers in the document
var numLayers = doc.layers.length;

// Loop through each layer in the document
for (var i = numLayers-1; i >= 0; i--) {
  var layer = doc.layers[i];
  
  // Check if the layer is a group
  if (layer.typename == "LayerSet") {
    
    // Merge the group contents into a new layer
    layer.merge();
    
  }
}

// Refresh the document
doc.activeLayer = doc.activeLayer;
