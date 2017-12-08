# foodTracker

FoodTracker is a automated food recognition dietary tracking application that is designed to make dietary tracking less time consuming and cumbersome. This repository is the android studio file corresponding with FoodTracker. To install, you'd need to open this respository with android studio and run it on your phone like you would with any other project.

It uses Google's Inception v3 image recognition neural network with the last layer trained on the Food-101 and the Veg200 datasets. The pretrained graphs and corresponding labels are located in the assets folder. The machine learning framework used is Tensorflow, and can be included by adding "compile 'org.tensorflow:tensorflow-android:+' " into the dependencies section of your build.gradle. (This is already done for this project).

Furthermore, nutrition information is obtained from the USDA nutrition database. I chose to retrieve only the commonly used macronutrients Calories, Fat, Carbs, Sugar, and Protein. The application could be easily modified to include any information that is within the USDA database, such as micronutrients. 

An example use case can be seen at https://www.youtube.com/watch?v=dtYknNteUKs