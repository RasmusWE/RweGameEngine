package dk.codemouse.RweGameEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
/**
 * 
 * Some of these drawing functions related to drawing with increased pixel size are kindly borrowed 
 * from Barista's java implementation of OlcPixelGameEngine and the OlcPixelGameEngine itself
 * 
 * @author Rasmus
 *
 */

public class GameGraphics {
	
	private GameEngine engine;	
	private Font font;
	
	private Rectangle fontBounds;
	
	private boolean useAntiAliasing = false, fullScreen = false;
	
	public GameGraphics(GameEngine engine, boolean fullScreen) {
		this.engine 	= engine;
		this.fullScreen = fullScreen;

		font = new Font(Font.MONOSPACED, Font.PLAIN, (int)(getGPixelSize() * 20));
	}
	
	public int getGPixelSize() {
		int ps = GameEngine.getPixelSize();
		if (ps > 1 && fullScreen) {
			return findNearestDivisible(GameEngine.frame.frameDimension.width, GameEngine.frame.frameDimension.height, ps);
		}

		return ps;
	}
	
	public void useAntiAliasing(boolean tof) {
		useAntiAliasing = tof;
	}
	
	public boolean useAntiAliasing() {
		return useAntiAliasing;
	}
	
	public void clearScreen(Graphics2D g, Color color) {
		g.setColor(color);
		g.fillRect(0, 0, GameEngine.frame.frameDimension.width, GameEngine.frame.frameDimension.height);
	}
	
	public Rectangle getFontBounds(Graphics2D g) {
		if (fontBounds != null)
			return fontBounds;
		
		if (font == null)
			return new Rectangle(0,0,0,0);
		
		fontBounds = font.getStringBounds("M", g.getFontRenderContext()).getBounds();
		return fontBounds;
	}
	
	public void draw(Graphics2D g, float x, float y, Color color) {		
		x *= GameEngine.getPixelSize();
		y *= GameEngine.getPixelSize();

		if (x > GameEngine.frame.frameDimension.width || x < 0)
			return;
		
		if (y > GameEngine.frame.frameDimension.height|| y < 0)
			return;

		g.setColor(color);
		g.fillRect((int) x, (int) y, GameEngine.getPixelSize(), GameEngine.getPixelSize());
	}
	
	public void drawLine(Graphics2D g, int x1, int y1, int x2, int y2, Color color) {		
		int x, y, dx, dy, dx1, dy1, px, py, xe, ye;
		
		dx = x2 - x1; 
		if (dx == 0) {
			drawVertLine(g, x1, y1, y2, color);
			return;
		}
		
		dy = y2 - y1;
		if (dy == 0) {
			drawHorizLine(g, x1, x2, y1, color);
			return;
		}
		
		if (useAntiAliasing) {
			drawLineA(g, x1, y1, x2, y2, color);
			return;
		}
		
		dx1 = Math.abs(dx); 
		dy1 = Math.abs(dy);
		
		px = 2 * dy1 - dx1;	py = 2 * dx1 - dy1;
		
		if (dy1 <= dx1) {
			if (dx >= 0) {
				x = x1; y = y1; xe = x2; 
			} else { 
				x = x2; y = y2; xe = x1;
			}

			engine.draw(g, x, y, color);
			
			while (x < xe) {
				x = x + 1;
				if (px<0)
					px = px + 2 * dy1;
				else {
					if ((dx<0 && dy<0) || (dx>0 && dy>0)) y = y + 1; else y = y - 1;
					px = px + 2 * (dy1 - dx1);
				}
				engine.draw(g, x, y, color);
			}
		} else {
			if (dy >= 0) { 
				x = x1; y = y1; ye = y2; 
			} else { 
				x = x2; y = y2; ye = y1; 
			}
	
			engine.draw(g, x, y, color);
			
			while (y < ye) {
				y = y + 1;
				
				if (py <= 0)
					py = py + 2 * dx1;
				else {
					if ((dx<0 && dy<0) || (dx>0 && dy>0)) x = x + 1; else x = x - 1;
						py = py + 2 * (dx1 - dy1);
				}
			
				engine.draw(g, x, y, color);
			}
		}
	}
	
	public void drawTriangle(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		if (useAntiAliasing) {
			drawTriangleA(g, x1, y1, x2, y2, x3, y3, color);
		} else {
			int ix1 = (int) x1;
			int ix2 = (int) x2;
			int ix3 = (int) x3;
			int iy1 = (int) y1;
			int iy2 = (int) y2;
			int iy3 = (int) y3;
			engine.drawLine(g, ix1, iy1, ix2, iy2, color);
			engine.drawLine(g, ix2, iy2, ix3, iy3, color);
			engine.drawLine(g, ix3, iy3, ix1, iy1, color);	
		}
	}
	
	public void fillTriangle(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		if (useAntiAliasing) {
			fillTriangleA(g, x1, y1, x2, y2, x3, y3, color);
		} else {	
			int ix1 = (int) x1;
			int ix2 = (int) x2;
			int ix3 = (int) x3;
			int iy1 = (int) y1;
			int iy2 = (int) y2;
			int iy3 = (int) y3;
			
			// copied nearly verbatim from olcConsoleGameEngine
			// SWAP and drawline autos replaced with swap and drawHorizLine above
			// primitives cannot be passed by reference in Java, so swap was modified to return a custom "Pair" class (below)
			// also, Java does not have goto so I used an if statement and loop labels with "break label;"
			int t1x, t2x, y, minX, maxX, t1xp, t2xp;
			boolean changed1 = false;
			boolean changed2 = false;
			int signX1, signX2, dx1, dy1, dx2, dy2;
			int e1, e2;
	
			// sort vertices
			Pair<Integer> swapPair;
			if (iy1 > iy2) {
				swapPair = Pair.swap(iy1, iy2);
				iy1 = swapPair.first;
				iy2 = swapPair.last;
				swapPair = Pair.swap(ix1, ix2);
				ix1 = swapPair.first;
				ix2 = swapPair.last;
			}
			if (iy1 > iy3) {
				swapPair = Pair.swap(iy1, iy3);
				iy1 = swapPair.first;
				iy3 = swapPair.last;
				swapPair = Pair.swap(ix1, ix3);
				ix1 = swapPair.first;
				ix3 = swapPair.last;
			}
			if (iy2 > iy3) {
				swapPair = Pair.swap(iy2, iy3);
				iy2 = swapPair.first;
				iy3 = swapPair.last;
				swapPair = Pair.swap(ix2, ix3);
				ix2 = swapPair.first;
				ix3 = swapPair.last;
			}
	
			t1x = t2x = ix1; // starting points
			y = iy1;
			dx1 = (int)(ix2 - ix1);
			if (dx1 < 0) {
				dx1 = -dx1;
				signX1 = -1;
			} else {
				signX1 = 1;
			}
			dy1 = (int)(iy2 - iy1);
	
			dx2 = (int)(ix3 - ix1);
			if (dx2 < 0) {
				dx2 = - dx2;
				signX2 = -1;
			} else {
				signX2 = 1;
			}
			dy2 = (int)(iy3 - iy1);
	
			if (dy1 > dx1) {
				swapPair = Pair.swap(dx1, dy1);
				dx1 = swapPair.first;
				dy1 = swapPair.last;
				changed1 = true;
			}
			if (dy2 > dx2) {
				swapPair = Pair.swap(dx2, dy2);
				dx2 = swapPair.first;
				dy2 = swapPair.last;
				changed2 = true;
			}
	
			e2 = (int)(dx2 >> 1);
			if (iy1 != iy2) {
				e1 = (int)(dx1 >> 1);
	
				for (float i = 0; i < dx1;) {
					t1xp = 0;
					t2xp = 0;
					if (t1x < t2x) {
						minX = t1x;
						maxX = t2x;
					} else {
						minX = t2x;
						maxX = t1x;
					}
	
					// process first line until y value is about to change
					nestLoop:
						while (i < dx1) {
							i++;
							e1 += dy1;
							while (e1 >= dx1) {
								e1 -= dx1;
								if (changed1) {
									t1xp = signX1;
								} else {
									break nestLoop;
								}
							}
							if (changed1) {
								break;
							} else {
								t1x += signX1;
							}
						}
	
					// process second line until y value is about to change
					nestLoop:
						while (true) {
							e2 += dy2;
							while (e2 >= dx2) {
								e2 -= dx2;
								if (changed2) {
									t2xp = signX2;
								} else {
									break nestLoop;
								}
							}
							if (changed2) {
								break;
							} else {
								t2x += signX2;
							}
						}
	
						if (minX > t1x) {
							minX = t1x;
						}
						if (minX > t2x) {
							minX = t2x;
						}
						if (maxX < t1x) {
							maxX = t1x;
						}
						if (maxX < t2x) {
							maxX = t2x;
						}
						drawHorizLine(g, minX, maxX, y, color); // draw line from min to max points found on the y
						//now increase y
						if (!changed1) {
							t1x += signX1;
						}
						t1x+= t1xp;
						if (!changed2) {
							t2x += signX2;
						}
						t2x += t2xp;
						y += 1;
						if (y == iy2) {
							break;
						}
				}
			}
			// second half
			dx1 = (int)(ix3 - ix2);
			if (dx1 < 0) {
				dx1 = -dx1;
				signX1 = -1;
			} else {
				signX1 = 1;
			}
			dy1 = (int)(iy3 - iy2);
			t1x = ix2;
	
			if (dy1 > dx1) {
				swapPair = Pair.swap(dy1, dx1);
				dy1 = swapPair.first;
				dx1 = swapPair.last;
				changed1 = true;
			} else {
				changed1 = false;
			}
	
			e1 = (int)(dx1 >> 1);
	
			for (float i = 0; i <= dx1; i++) {
				t1xp = 0;
				t2xp = 0;
				if (t1x < t2x) {
					minX = t1x;
					maxX = t2x;
				} else {
					minX = t2x; maxX = t1x;
				}
	
				// process first line until y value is about to change
				nestLoop:
					while (i < dx1) {
						e1 += dy1;
						while (e1 >= dx1) {
							e1 -= dx1;
							if (changed1) {
								t1xp = signX1;
								break;
							} else {
								break nestLoop;
							}
						}
						if (changed1) {
							break;
						} else {
							t1x += signX1;
						}
						if (i < dx1) {
							i++;
						}
					}
	
				// process second line until y value is about to change
				nestLoop:
					while (t2x != x3) {
						e2 += dy2;
						while (e2 >= dx2) {
							e2 -= dx2;
							if (changed2) {
								t2xp = signX2;
							} else {
								break nestLoop;
							}
						}
						if (changed2) {
							break;
						} else {
							t2x += signX2;
						}
					}
	
					if (minX > t1x) {
						minX = t1x;
					}
					if (minX > t2x) {
						minX = t2x;
					}
					if (maxX < t1x) {
						maxX = t1x;
					}
					if (maxX < t2x) {
						maxX = t2x;
					}
					drawHorizLine(g, minX, maxX, y, color);
					if (!changed1) {
						t1x += signX1;
					}
					t1x += t1xp;
					if (!changed2) {
						t2x += signX2;
					}
					t2x += t2xp;
					y += 1;
					if (y > iy3) {
						return;
					}
			}
		}
	}

	public void drawCircle(Graphics2D g, float xCenter, float yCenter, float radius, Color color) { // midpofloat circle algorithm
		if (useAntiAliasing) {
			drawCircleA(g, xCenter, yCenter, radius, color);
		} else {
			// copied verbatim from olcConsoleGameEngine
			float x = 0;
			float y = radius;
			float p = 3 - 2 * radius;

			while (y >= x) { //formulate only 1/8 of circle
				engine.draw(g, xCenter - x, yCenter - y, color);
				engine.draw(g, xCenter - y, yCenter - x, color);
				engine.draw(g, xCenter + y, yCenter - x, color);
				engine.draw(g, xCenter + x, yCenter - y, color);
				engine.draw(g, xCenter - x, yCenter + y, color);
				engine.draw(g, xCenter - y, yCenter + x, color);
				engine.draw(g, xCenter + y, yCenter + x, color);
				engine.draw(g, xCenter + x, yCenter + y, color);
				if (p < 0) {
					p += 4 * x++ + 6;
				} else {
					p += 4 * (x++ - y--) + 10;
				}
			}	
		}
	}
	
	public void fillCircle(Graphics2D g, float xCenter, float yCenter, float radius, Color color) {
		if (useAntiAliasing) {
			fillCircleA(g, xCenter, yCenter, radius, color);
		} else {
			// copied verbatim from olcConsoleGameEngine, with the drawline auto replaced with drawHorizLine() (above)
			int x = 0;
			float y = radius;
			float p = 3 - 2 * radius;

			while (y >= x) {
				drawHorizLine(g, (int) (xCenter - x), (int) (xCenter + x), (int) (yCenter - y), color);
				drawHorizLine(g, (int) (xCenter - y), (int) (xCenter + y), (int) (yCenter - x), color);
				drawHorizLine(g, (int) (xCenter - x), (int) (xCenter + x), (int) (yCenter + y), color);
				drawHorizLine(g, (int) (xCenter - y), (int) (xCenter + y), (int) (yCenter + x), color);
				if (p < 0) {
					p += 4 * x++ + 6;
				} else {
					p += 4 * (x++ - y--) + 10;
				}
			}	
		}
	}
	
	public void drawRect(Graphics2D g, float x, float y, int w, int h, Color color) {
		RenderingHints oldrh = null;
		if (useAntiAliasing) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);	
		}
		
		engine.drawLine(g, (int) x, (int) y, (int) x + w, (int) y, color);
		engine.drawLine(g, (int) x + w, (int) y, (int) x + w, (int) y + h, color);
		engine.drawLine(g, (int) x + w, (int) y + h, (int) x, (int) y + h, color);
		engine.drawLine(g, (int) x, (int) y + h, (int) x, (int) y, color);
			
		if (useAntiAliasing)
			g.setRenderingHints(oldrh);
	}
	
	public void fillRect(Graphics2D g, float x, float y, int w, int h, Color color) {
		if (useAntiAliasing) {
			fillRectA(g, x, y, w, h, color);	
		} else {
			float x2 = x + w;
			float y2 = y + h;
	
			float yIncr = y;
			while (yIncr <= y2) {
				engine.drawLine(g, (int) x, (int) yIncr, (int) x2, (int) yIncr, color);
				yIncr += 1;
			}
		}
	}
	
	public void drawPolygon(Graphics2D g, ArrayList<Pair<Float>> modelCoordinates, float x, float y, float angle, boolean fill, Color color) {
		drawPolygon(g, modelCoordinates, x, y, angle, 1.0f, fill, color);
	}
	
	public void drawPolygon(Graphics2D g, ArrayList<Pair<Float>> modelCoordinates, float x, float y, float angle, float scale, boolean fill, Color color) {
		//First part borrowed from OLC: https://www.youtube.com/watch?v=QgDR8LrRZhk&t=1767s
		
		//pair.first  = x
		//pair.last = y
		
		//Create translated model vector of coordinate pairs
		int verts = modelCoordinates.size();
		ArrayList<Pair<Float>> transformedCoordinates = new ArrayList<>();
		for (Pair<Float> p : modelCoordinates) {
			transformedCoordinates.add(new Pair<Float>(p.first, p.last));
		}		
		
		//Rotate
		for (int i = 0; i < verts; i++) {
			transformedCoordinates.get(i).first = (float) (modelCoordinates.get(i).first * Math.cos(angle) - modelCoordinates.get(i).last * Math.sin(angle));
			transformedCoordinates.get(i).last  = (float) (modelCoordinates.get(i).first * Math.sin(angle) + modelCoordinates.get(i).last * Math.cos(angle));
		}
		
		//Scale
		for (int i = 0; i < verts; i++) {
			transformedCoordinates.get(i).first *= scale;
			transformedCoordinates.get(i).last  *= scale;
		}
		
		//Translate
		for (int i = 0; i < verts; i++) {
			transformedCoordinates.get(i).first += x;
			transformedCoordinates.get(i).last  += y;
		}
		
		//Draw closed polygon
		if (useAntiAliasing()) {
			drawWireFrameModelA(g, transformedCoordinates, x, y, fill, color);
		} else {
			if (fill)
				System.err.println("drawPolygon: FILLMODE cannot be used with anti aliasing diabled!");
			
			for (int i = 0; i < verts + 1; i++) {
				int j = (i + 1);
				engine.drawLine(g, Math.round(transformedCoordinates.get(i % verts).first), Math.round(transformedCoordinates.get(i % verts).last), Math.round(transformedCoordinates.get(j % verts).first), Math.round(transformedCoordinates.get(j % verts).last), color);
			}
		}
	}
	
	public void drawSprite(Graphics2D g, GameSprite sprite, int x, int y, double scale, float angle) {
		if (!sprite.isLoaded())
			return;
		
		if (angle > 0)
			sprite = sprite.rotate(angle, false);
		
		drawS(g, sprite, x, y, scale);
	}
	
	public void drawSprite(Graphics2D g, GameSprite sprite, int x, int y, double scale, double angleByDegrees) {
		if (!sprite.isLoaded())
			return;
		
		if (angleByDegrees > 0)
			sprite = sprite.rotate(angleByDegrees, true);
		
		drawS(g, sprite, x, y, scale);	
	}
	
	public void drawPartialSprite(Graphics2D g, GameSprite sprite, int x, int y, int ox, int oy, int width, int height, double scale, float angle) {
		if (!sprite.isLoaded())
			return;
		
		if (angle > 0)
			sprite = sprite.rotate(angle, false);
		
		drawPS(g, sprite, x, y, ox, oy, width, height, scale);
	}
	
	public void drawPartialSprite(Graphics2D g, GameSprite sprite, int x, int y, int ox, int oy, int width, int height, double scale, double angleByDegrees) {
		if (!sprite.isLoaded())
			return;
		
		if (angleByDegrees > 0)
			sprite = sprite.rotate(angleByDegrees, true);
			
		drawPS(g, sprite, x, y, ox, oy, width, height, scale);
	}
	
	public void drawString(Graphics2D g, String string, int x, int y, Color color) {
		x = (x * GameEngine.getPixelSize()) - (GameEngine.getPixelSize() + 1);
		y = (y * GameEngine.getPixelSize()) - (GameEngine.getPixelSize() + 1);
		
		g.setColor(color);
		g.setFont(font);
		
		if (useAntiAliasing) {
			g.drawString(string, x, y);
		} else {		
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

			RenderingHints oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);
			g.drawString(string, x, y);
			g.setRenderingHints(oldrh);
		}
	}
	
	public void setFont(Font font) {
		this.font = font;
		this.font = font.deriveFont((float) font.getSize() * GameEngine.getPixelSize());
	}
	
	public void setFontSize(float size) {
		this.font = font.deriveFont(size * GameEngine.getPixelSize());
	}
	
	private void drawHorizLine(Graphics2D g, int startX, int endX, int y, Color color) {
		RenderingHints oldrh = null;
		if (useAntiAliasing) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);	
		}
		
		if (endX - startX >= 0) {
			for (float i = startX; i <= endX; i++) {
				engine.draw(g, i, y, color);
			}
		} else {
			for (float i = startX; i >= endX; i--) {
				engine.draw(g, i, y, color);
			}
		}	

		if (useAntiAliasing)
			g.setRenderingHints(oldrh);	
	}

	private void drawVertLine(Graphics2D g, int x, int startY, int endY, Color color) {
		RenderingHints oldrh = null;
		if (useAntiAliasing) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);	
		}
		
		if (endY - startY >= 0) {
			for (float i = startY; i <= endY; i++) {
				engine.draw(g, x, i, color);
			}
		} else {
			for (float i = startY; i >= endY; i--) {
				engine.draw(g, x, i, color);
			}
		}	
		
		if (useAntiAliasing)
			g.setRenderingHints(oldrh);	
	}
	
	private void drawLineA(Graphics2D g, float x1, float y1, float x2, float y2, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		x1 *= ps;
		y1 *= ps;
		x2 *= ps;
		y2 *= ps;
		
		x1 += ps / 2;
		y1 += ps / 2;
		x2 += ps / 2;
		y2 += ps / 2;
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
		g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
		
		g.setStroke(oldStroke);
	}
	
	private void drawCircleA(Graphics2D g, float xCenter, float yCenter, float radius, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		float x = xCenter - radius;
		float y = yCenter - radius;
		
		x *= ps;
		y *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		float w = (radius * 2) * ps;
		float h = (radius * 2) * ps;
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);
		g.draw(ell);
		
		g.setStroke(oldStroke);
	}
	
	private void fillCircleA(Graphics2D g, float xCenter, float yCenter, float radius, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		float x = xCenter - radius;
		float y = yCenter - radius;
		
		x *= ps;
		y *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		float w = (radius * 2) * ps;
		float h = (radius * 2) * ps;
	
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);		
		g.fill(ell);
	}
	
	private void fillRectA(Graphics2D g, float x, float y, int w, int h, Color color) {
		g.setColor(color);

		int ps = GameEngine.getPixelSize();
		
		x *= ps;
		y *= ps;
		w *= ps;
		h *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		g.fillRect((int) (x - ps / 2), (int) (y - ps / 2), w + ps, h + ps);
	}
	
	private void drawTriangleA(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		g.setColor(color);
	    
		int ps = GameEngine.getPixelSize();
		
		x1 *= ps;
		y1 *= ps;
		x2 *= ps;
		y2 *= ps;
		x3 *= ps;
		y3 *= ps;
		
		x1 += ps / 2;
		x2 += ps / 2;
		x3 += ps / 2;
		y1 += ps / 2;
		y2 += ps / 2;
		y3 += ps / 2;
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
	    TriangleShape triangleShape = new TriangleShape(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3));
	    g.draw(triangleShape);
	    
		g.setStroke(oldStroke);
	}
	
	private void fillTriangleA(Graphics2D g, float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
		g.setColor(color);
		
		drawTriangleA(g, x1, y1, x2, y2, x3, y3, color);
		
		int ps = GameEngine.getPixelSize();
		
		x1 *= ps;
		y1 *= ps;
		x2 *= ps;
		y2 *= ps;
		x3 *= ps;
		y3 *= ps;
		
		x1 += ps / 2;
		x2 += ps / 2;
		x3 += ps / 2;
		y1 += ps / 2;
		y2 += ps / 2;
		y3 += ps / 2;
		
	    TriangleShape triangleShape = new TriangleShape(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3));
	    
	    g.fill(triangleShape);
	}
	
	private void drawWireFrameModelA(Graphics2D g, ArrayList<Pair<Float>> modelCoordinates, float x, float y, boolean fill, Color color) {
		g.setColor(color);
	    
		int ps = GameEngine.getPixelSize();
		
		int[] xPoly = new int[modelCoordinates.size()];
		int[] yPoly = new int[modelCoordinates.size()];
		int idx = 0;
		for (Pair<Float> mc : modelCoordinates) {
			xPoly[idx] = Math.round(mc.first * ps);
			yPoly[idx] = Math.round(mc.last * ps);
			idx++;
		}
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
		Polygon polygon = new Polygon(xPoly, yPoly, xPoly.length);
		if (fill)
			g.fillPolygon(polygon);
		else
			g.drawPolygon(polygon);
	    
		g.setStroke(oldStroke);
	}
	
	private void drawS(Graphics2D g, GameSprite sprite, int x, int y, double scale) {
		if (scale > 1)
			for (int i = 0; i < sprite.getWidth(); i++)
				for (int j = 0; j < sprite.getHeight(); j++)
					for (int is = 0; is < scale; is++)
						for (int js = 0; js < scale; js++)
							draw(g, x + Math.round(i * scale) + is, y + Math.round(j * scale) + js, sprite.getPixel(i, j));		
		
		else 
			for (int i = 0; i < sprite.getWidth(); i++)
				for (int j = 0; j < sprite.getHeight(); j++)
					draw(g, x + i, y + j, sprite.getPixel(i, j));	
	}
	
	private void drawPS(Graphics2D g, GameSprite sprite, int x, int y, int ox, int oy, int width, int height, double scale) {
		if (scale > 1)
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					for (int is = 0; is < scale; is++)
						for (int js = 0; js < scale; js++)
							draw(g, x + Math.round(i * scale) + is, y + Math.round(j * scale) + js, sprite.getPixel(i + ox, j + oy));

		else
			for (int i = 0; i < width; i++)
				for (int j = 0; j < height; j++)
					draw(g, x + i, y + j, sprite.getPixel(i + ox, j + oy));
	}
	
	private int findNearestDivisible(int number1, int number2, int to) {
		int mTo = to;
		while (((number1 % mTo != 0) || (number2 % mTo != 0)) && mTo > 1) {	
			mTo--;
		}
		
		if (mTo < 1)
			mTo = 1;
		
		if (mTo != to)
			System.out.println("Pixel size can be no larger than " + mTo);
		
		return mTo;
	}
	
	private class TriangleShape extends Path2D.Double {
		private static final long serialVersionUID = 1L;
		public TriangleShape(Point2D... points) {
		    moveTo(points[0].getX(), points[0].getY());
		    lineTo(points[1].getX(), points[1].getY());
		    lineTo(points[2].getX(), points[2].getY());
		    closePath();
		}
	}
}
