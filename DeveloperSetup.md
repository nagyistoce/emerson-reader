# Developer setup #
Install Java 5 JDK or later. OpenJDK should work, but has not been extensively tested.

Install Eclipse 3.5.2 or later.

Create a target platform using [vanilla 3.5.2](http://download.eclipse.org/eclipse/downloads/drops/R-3.5.2-201002111343/index.php), extract to a different location than Eclipse's default plugin folder. Include com.ibm.icu.base and the delta pack, also from the 3.5.2 download link above.

In "Preferences/Plug-in Development/Target platform", activate the target platform created above. Reload.

Get the code from the trunk. (All packages in the trunk root are OSGi bundles, except org.daisy.emerson.install.)

Create a new Run Configuration. In the Run Configurations main tab, select "Run a Product" and choose org.daisy.emerson.ui.product.

Go to the Run Configurations plugins tab, select "deselect all", select "add required", and then recheck any plugin that is not selected in the workspace area.

You should now be able to run the product.