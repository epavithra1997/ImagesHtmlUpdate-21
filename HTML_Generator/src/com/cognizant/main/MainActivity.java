package com.cognizant.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import com.cognizant.beans.Layers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity {
	static String htmlContent = " ";
	static String cssContent = " ";
	static Stack<String> stack = new Stack<String>();
	static Stack<Float> guideX = new Stack<Float>();
	static Stack<Float> guideY = new Stack<Float>();
	static Stack<String> cssStack = new Stack<String>();
	static String parenttext = " ", childtext = " ", subchildtext = " ";
	static int valX = 0;
	static int valY = 0;
	static String images=" ";
	static String childimages=" ";
	static String subchildimages=" ";
	static String pathvar=" ";
	static String htmlfile=" ";
	static String cssfile=" ";
	private static Scanner obj;
	public static void main(String[] args) throws FileNotFoundException {

		obj = new Scanner(System.in);
	
		// Reading JSON File using GSON
		Gson gson = new Gson();
		System.out.println("Enter the json file Location:\n");
		pathvar=obj.next();		
		String path = pathvar;				//C:\Users\765749\Documents\demo.json
		BufferedReader br = new BufferedReader(new FileReader(path));
		Map<String, Layers> decoded = gson.fromJson(br, new TypeToken<Map<String, Layers>>() {
		}.getType());

		// Creating HTML and CSS File
		try {
			System.out.println("Enter the location that HTML file to be placed:\n");
			htmlfile=obj.next();
			File htmlFile =new File(htmlfile);        //C:\\Users\\765749\\Documents\\generatedHTML.html
			System.out.println("Enter the location that CSS file to be placed:\n");
			cssfile=obj.next();
			File cssFile = new File(cssfile);         //C:\\Users\\765749\\Documents\\generatedCSS.css

			if (htmlFile.createNewFile() && cssFile.createNewFile()) {

				// Writing HTML and CSS File
				FileWriter htmlWriter = new FileWriter(htmlFile);
				FileWriter cssWriter = new FileWriter(cssFile);
				BufferedWriter htmlBuffer = new BufferedWriter(htmlWriter);
				BufferedWriter cssBuffer = new BufferedWriter(cssWriter);
				htmlBuffer.write(
						"<html>\n<head>\n<link rel = \"stylesheet\" type = \"text/css\" href = \"generatedCSS.css\">\n<link rel = \"stylesheet\" type = \"text/css\" href =\"C:\\Users\\765749\\Documents\\externalcss\\colors.css\">\n<link rel = \"stylesheet\" type = \"text/css\" href =\"C:\\Users\\765749\\Documents\\externalcss\\fonts.css\">\n<meta http-equiv=\"content-type\" \r\n"
								+ "content=\"text/html;charset=utf-8\" /></head>\n<body>\n");
				
				//Iterating Layers
				for (String layerName : decoded.keySet()) {
					Layers layerValue = decoded.get(layerName);
					List<String> layerParent = layerValue.getParent();
					Map<String, String> cssStyles = layerValue.getCssStyles();
					for (String parent : layerParent) {

						if (parent.equals("ROOT_REF_WINDOW")) {
							
							//Stack to create nested div
							guideX.push((float) 0.00);
							guideY.push((float) 0.00);
							parenttext = layerValue.getText();
						
							images=layerValue.getImages();
							
							cssStack.push(layerValue.getElementName());							
							
							computeStyleGuide(decoded, guideX, guideY, cssStack);//Function call
							if(images!=null)
							{
								System.out.println("hi1"+images);
								htmlContent = htmlContent + "\n<img src="+"\"C:\\Users\\765749\\Documents\\"+layerValue.getImages()+"\" id=\""+layerValue.getElementName()+"\"/>" ;
							}
							else if (parenttext != null) {
								htmlContent = htmlContent + "\n<span id = \"" + layerValue.getElementName() + "\">"
										+ layerValue.getText() + "</span>\n";

							} else {
								htmlContent = htmlContent + "\n<div id = \"" + layerValue.getElementName() + "\">\n";
								List<String> layerContains = layerValue.getContains();
								childtext = layerValue.getText();
								childimages=layerValue.getImages();
								if(childimages!=null)
								{
									System.out.println("hi2"+images);
									htmlContent = htmlContent + "\n<img src="+"\"C:\\Users\\765749\\Documents\\"+layerValue.getImages()+"\" id=\""+layerValue.getElementName()+"\"/>" ;
								}
								else if (childtext != null) {
									htmlContent = htmlContent + "\n<span id = \"" + layerValue.getElementName() + "\">"
											+ layerValue.getText() + "</span>\n";
								} else if (layerContains.isEmpty()) {
									htmlContent = htmlContent + "\n</div>";
								} else {
									for (String cont : layerContains) {
										stack.push(cont);
									}

									pushFunction(decoded, stack);

									htmlContent = htmlContent + "</div>";
								}
							}
						}
					}

				}
				for (String layerName : decoded.keySet()) {

					if (!layerName.equals("ROOT_REF_WINDOW")) {

						Layers layerValue = decoded.get(layerName);
						List<String> layerParent = layerValue.getParent();

						Map<String, String> cssStyles = layerValue.getCssStyles();
						parenttext = layerValue.getText();
						
						//System.out.println(" " + parenttext);
						
						//cssContent
						
						
						if (parenttext != null) {

							cssContent = cssContent + "#" + layerValue.getElementName() + "{\nposition:absolute;\n";
						} 
						else {

							cssContent = cssContent + "#" + layerValue.getElementName()
									+ "{\nposition:absolute;\nborder:solid 1px black;\n";
						}
						cssContent = cssContent + "height:" + layerValue.getHeight() + ";\n";
						cssContent = cssContent + "width:" + layerValue.getWidth() + ";\n";
						cssContent = cssContent + "left:" + layerValue.getStyleGuideX() + ";\n";
						cssContent = cssContent + "top:" + layerValue.getStyleGuideY() + ";\n";

						for (String map : cssStyles.keySet()) {

							cssContent = cssContent + map + ":" + cssStyles.get(map) + ";\n";
						}
						cssContent = cssContent + "}\n";

					} else {
						Layers layerValue = decoded.get(layerName);
						List<String> layerParent = layerValue.getParent();

						Map<String, String> cssStyles = layerValue.getCssStyles();
						parenttext = layerValue.getText();
						if (parenttext != null) {

							cssContent = cssContent + "#" + layerValue.getElementName() + "{\nposition:absolute;\n";
						}
					}

					// File Close

				}
				cssBuffer.write(cssContent);
				htmlBuffer.write(htmlContent);
				htmlBuffer.write("\n</body>\n</html>");
				htmlBuffer.close();
				cssBuffer.close();
			} else {
				System.out.println("file already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	//Css Property for inner div
	private static void computeStyleGuide(Map<String, Layers> decoded, Stack<Float> guideX2, Stack<Float> guideY2,
			Stack<String> cssStack2) {
		if (!cssStack2.peek().equals("ROOT_REF_WINDOW")) {
			while (!cssStack2.isEmpty()) {
				//Finding Parent element
				if (cssStack2.peek().equals("-1")) {
					guideX2.pop();
					guideY2.pop();
					cssStack2.pop();
				} 
				else {
					Layers layer = decoded.get(cssStack2.peek());
					List<String> layerContains = layer.getContains();
					String s = layer.getStyleGuideX();
					String newX = s.substring(0, s.length() - 2);
					String lasting = s.substring(s.length() - 2, s.length());
					Float layX = Float.parseFloat(newX);
					String s1 = layer.getStyleGuideY();
					String lasting1 = s1.substring(s1.length() - 2, s1.length());
					String newY = s1.substring(0, s1.length() - 2);
					Float layY = Float.parseFloat(newY);
					if (layerContains.isEmpty()) {
						//Setting empty div 
						float resX = Math.abs(layX - guideX2.peek());

						float resY = Math.abs(layY - guideY2.peek());
						layer.setStyleGuideX(String.valueOf(resX) + lasting);
						layer.setStyleGuideY(String.valueOf(resY) + lasting1);
						cssStack2.pop();

					} 
					else {
						//Setting parent div
						float setX = Math.abs(layX - guideX2.peek());
						layer.setStyleGuideY(String.valueOf(setX) + lasting);
						float setY = Math.abs(layY - guideY2.peek());
						layer.setStyleGuideY(String.valueOf(setY) + lasting1);
						cssStack2.pop();
						cssStack2.push("-1");
						float tempX = layX;
						guideX2.push(tempX);
						float tempY = layY;
						guideY2.push(tempY);

						for (String tmpString : layerContains) {
							cssStack2.push(tmpString);

						}

					}
				}
			}
		}

	}
	//Stack Function for nested div
	private static void pushFunction(Map<String, Layers> decoded, Stack<String> stack2) {

		while (!stack2.empty()) {
			if (stack2.peek().equals("-1")) {
				htmlContent = htmlContent + "\n</div>";
				stack2.pop();

			} 
			else {
				Layers subLayer = decoded.get(stack2.peek());
				List<String> subLayerContains = subLayer.getContains();
				subchildtext = subLayer.getText();
				subchildimages = subLayer.getImages();
				if(subchildimages!=null)
				{
					System.out.print("hi3"+images);
					htmlContent = htmlContent + "\n<img src="+"\"C:\\Users\\765749\\Documents\\"+subLayer.getImages()+"\" id=\""+subLayer.getElementName()+"\"/>" ;
					//htmlContent = htmlContent + "\n<div id=\""+subLayer.getElementName()+"\"><img src="+"\"C:\\Users\\765749\\Documents\\"+subLayer.getImages()+"\"/>This is image</div>" ;
				}
				else if (subchildtext != null) {

					htmlContent = htmlContent + "\n<span id = \"" + subLayer.getElementName() + "\">"
							+ subLayer.getText() + "</span>\n";

				}
				else {
					htmlContent = htmlContent + "\n<div id = \"" + subLayer.getElementName() + "\"></div>\n";
				}

				if (subLayerContains.isEmpty()) {

					stack2.pop();
				}
				else {
					htmlContent = htmlContent + "\n<div id = \"" + subLayer.getElementName() + "\">";
					stack2.pop();
					stack2.push("-1");
					for (String contain : subLayerContains) {
						stack2.push(contain);
					}
				}
			}
		}

	}

}
