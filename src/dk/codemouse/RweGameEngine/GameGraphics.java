package dk.codemouse.RweGameEngine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * 
 * Many of these drawing functions related to drawing with increased pixel size are kindly borrowed 
 * from Barista's java implementation of OlcPixelGameEngine and the OlcPixelGameEngine itself
 * 
 * Though the functions that implements anti aliasing are my own implementation.
 * 
 * TODO: Sprite handling
 * 
 * @author Rasmus
 *
 */

public class GameGraphics {
	
	private GameEngine engine;	
	private Font font;
	
	private boolean useAntiAliasing = false, fullScreen = false;
	
	public GameGraphics(GameEngine engine, boolean fullScreen) {
		this.engine 	= engine;
		this.fullScreen = fullScreen;

		font = new Font(Font.MONOSPACED, Font.PLAIN, (int)(getGPixelSize() * 20));
	}
	
	public int getGPixelSize() {
		int ps = GameEngine.getPixelSize();
		if (ps > 1 && fullScreen) {
			return findNearestDivisible(engine.frame.frameDimension.width, engine.frame.frameDimension.height, ps);
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
		g.fillRect(0, 0, engine.frame.frameDimension.width, engine.frame.frameDimension.height);
	}
	
	public void draw(Graphics2D g, int x, int y, Color color) {
		x *= GameEngine.getPixelSize();
		y *= GameEngine.getPixelSize();
		
		if (x > engine.frame.frameDimension.width || x < 0)
			return;
		
		if (y > engine.frame.frameDimension.height|| y < 0)
			return;
		
		g.setColor(color);
		g.fillRect(x, y, GameEngine.getPixelSize(), GameEngine.getPixelSize());
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

			draw(g, x, y, color);
			
			while (x < xe) {
				x = x + 1;
				if (px<0)
					px = px + 2 * dy1;
				else {
					if ((dx<0 && dy<0) || (dx>0 && dy>0)) y = y + 1; else y = y - 1;
					px = px + 2 * (dy1 - dx1);
				}
				draw(g, x, y, color);
			}
		} else {
			if (dy >= 0) { 
				x = x1; y = y1; ye = y2; 
			} else { 
				x = x2; y = y2; ye = y1; 
			}
	
			draw(g, x, y, color);
			
			while (y < ye) {
				y = y + 1;
				
				if (py <= 0)
					py = py + 2 * dx1;
				else {
					if ((dx<0 && dy<0) || (dx>0 && dy>0)) x = x + 1; else x = x - 1;
						py = py + 2 * (dx1 - dy1);
				}
			
				draw(g, x, y, color);
			}
		}
	}
	
	public void drawTriangle(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		if (useAntiAliasing) {
			drawTriangleA(g, x1, y1, x2, y2, x3, y3, color);
		} else {
			drawLine(g, x1, y1, x2, y2, color);
			drawLine(g, x2, y2, x3, y3, color);
			drawLine(g, x3, y3, x1, y1, color);	
		}
	}
	
	public void fillTriangle(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		if (useAntiAliasing) {
			fillTriangleA(g, x1, y1, x2, y2, x3, y3, color);
		} else {		
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
			if (y1 > y2) {
				swapPair = Pair.swap(y1, y2);
				y1 = swapPair.first;
				y2 = swapPair.last;
				swapPair = Pair.swap(x1, x2);
				x1 = swapPair.first;
				x2 = swapPair.last;
			}
			if (y1 > y3) {
				swapPair = Pair.swap(y1, y3);
				y1 = swapPair.first;
				y3 = swapPair.last;
				swapPair = Pair.swap(x1, x3);
				x1 = swapPair.first;
				x3 = swapPair.last;
			}
			if (y2 > y3) {
				swapPair = Pair.swap(y2, y3);
				y2 = swapPair.first;
				y3 = swapPair.last;
				swapPair = Pair.swap(x2, x3);
				x2 = swapPair.first;
				x3 = swapPair.last;
			}
	
			t1x = t2x = x1; // starting points
			y = y1;
			dx1 = (int)(x2 - x1);
			if (dx1 < 0) {
				dx1 = -dx1;
				signX1 = -1;
			} else {
				signX1 = 1;
			}
			dy1 = (int)(y2 - y1);
	
			dx2 = (int)(x3 - x1);
			if (dx2 < 0) {
				dx2 = - dx2;
				signX2 = -1;
			} else {
				signX2 = 1;
			}
			dy2 = (int)(y3 - y1);
	
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
			if (y1 != y2) {
				e1 = (int)(dx1 >> 1);
	
				for (int i = 0; i < dx1;) {
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
						if (y == y2) {
							break;
						}
				}
			}
			// second half
			dx1 = (int)(x3 - x2);
			if (dx1 < 0) {
				dx1 = -dx1;
				signX1 = -1;
			} else {
				signX1 = 1;
			}
			dy1 = (int)(y3 - y2);
			t1x = x2;
	
			if (dy1 > dx1) {
				swapPair = Pair.swap(dy1, dx1);
				dy1 = swapPair.first;
				dx1 = swapPair.last;
				changed1 = true;
			} else {
				changed1 = false;
			}
	
			e1 = (int)(dx1 >> 1);
	
			for (int i = 0; i <= dx1; i++) {
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
					if (y > y3) {
						return;
					}
			}
		}
	}

	public void drawCircle(Graphics2D g, int xCenter, int yCenter, int radius, Color color) { // midpoint circle algorithm
		if (useAntiAliasing) {
			drawCircleA(g, xCenter, yCenter, radius, color);
		} else {
			// copied verbatim from olcConsoleGameEngine
			int x = 0;
			int y = radius;
			int p = 3 - 2 * radius;

			while (y >= x) { //formulate only 1/8 of circle
				draw(g, xCenter - x, yCenter - y, color);
				draw(g, xCenter - y, yCenter - x, color);
				draw(g, xCenter + y, yCenter - x, color);
				draw(g, xCenter + x, yCenter - y, color);
				draw(g, xCenter - x, yCenter + y, color);
				draw(g, xCenter - y, yCenter + x, color);
				draw(g, xCenter + y, yCenter + x, color);
				draw(g, xCenter + x, yCenter + y, color);
				if (p < 0) {
					p += 4 * x++ + 6;
				} else {
					p += 4 * (x++ - y--) + 10;
				}
			}	
		}
	}
	
	public void fillCircle(Graphics2D g, int xCenter, int yCenter, int radius, Color color) {
		if (useAntiAliasing) {
			fillCircleA(g, xCenter, yCenter, radius, color);
		} else {
			// copied verbatim from olcConsoleGameEngine, with the drawline auto replaced with drawHorizLine() (above)
			int x = 0;
			int y = radius;
			int p = 3 - 2 * radius;

			while (y >= x) {
				drawHorizLine(g, xCenter - x, xCenter + x, yCenter - y, color);
				drawHorizLine(g, xCenter - y, xCenter + y, yCenter - x, color);
				drawHorizLine(g, xCenter - x, xCenter + x, yCenter + y, color);
				drawHorizLine(g, xCenter - y, xCenter + y, yCenter + x, color);
				if (p < 0) {
					p += 4 * x++ + 6;
				} else {
					p += 4 * (x++ - y--) + 10;
				}
			}	
		}
	}
	
	public void drawRect(Graphics2D g, int x, int y, int w, int h, Color color) {
		RenderingHints oldrh = null;
		if (useAntiAliasing) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);	
		}
		
		drawLine(g, x, y, x + w, y, color);
		drawLine(g, x + w, y, x + w, y + h, color);
		drawLine(g, x + w, y + h, x, y + h, color);
		drawLine(g, x, y + h, x, y, color);
			
		if (useAntiAliasing)
			g.setRenderingHints(oldrh);
	}
	
	public void fillRect(Graphics2D g, int x, int y, int w, int h, Color color) {
		if (useAntiAliasing) {
			fillRectA(g, x, y, w, h, color);	
		} else {
			int x2 = x + w;
			int y2 = y + h;
	
			int yIncr = y;
			while (yIncr <= y2) {
				drawLine(g, x, yIncr, x2, yIncr, color);
				yIncr += 1;
			}
		}
	}
	
	public void setFont(Font font) {
		this.font = font;
		this.font = font.deriveFont((float) font.getSize() * GameEngine.getPixelSize());
	}
	
	public void setFontSize(float size) {
		this.font = font.deriveFont(size * GameEngine.getPixelSize());
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
	
	private void drawHorizLine(Graphics2D g, int startX, int endX, int y, Color color) {
		RenderingHints oldrh = null;
		if (useAntiAliasing) {
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			
			oldrh = g.getRenderingHints();	
			g.setRenderingHints(rh);	
		}
		
		if (endX - startX >= 0) {
			for (int i = startX; i <= endX; i++) {
				draw(g, i, y, color);
			}
		} else {
			for (int i = startX; i >= endX; i--) {
				draw(g, i, y, color);
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
			for (int i = startY; i <= endY; i++) {
				draw(g, x, i, color);
			}
		} else {
			for (int i = startY; i >= endY; i--) {
				draw(g, x, i, color);
			}
		}	
		
		if (useAntiAliasing)
			g.setRenderingHints(oldrh);	
	}
	
	private void drawLineA(Graphics2D g, int x1, int y1, int x2, int y2, Color color) {
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
		
		g.drawLine(x1, y1, x2, y2);
		
		g.setStroke(oldStroke);
	}
	
	private void drawCircleA(Graphics2D g, int xCenter, int yCenter, int radius, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		int x = xCenter - radius;
		int y = yCenter - radius;
		
		x *= ps;
		y *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		int w = (radius * 2) * ps;
		int h = (radius * 2) * ps;
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);
		g.draw(ell);
		
		g.setStroke(oldStroke);
	}
	
	private void fillCircleA(Graphics2D g, int xCenter, int yCenter, int radius, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		int x = xCenter - radius;
		int y = yCenter - radius;
		
		x *= ps;
		y *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		int w = (radius * 2) * ps;
		int h = (radius * 2) * ps;
	
		Ellipse2D ell = new Ellipse2D.Double(x, y, w, h);		
		g.fill(ell);
	}
	
	private void fillRectA(Graphics2D g, int x, int y, int w, int h, Color color) {
		g.setColor(color);

		int ps = GameEngine.getPixelSize();
		
		x *= ps;
		y *= ps;
		w *= ps;
		h *= ps;
		
		x += ps / 2;
		y += ps / 2;
		
		g.fillRect(x - ps / 2, y - ps / 2, w + ps, h + ps);
	}
	
	private void drawTriangleA(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		g.setColor(color);
	    
		int ps = GameEngine.getPixelSize();
		
		x1 *= ps;
		y1 *= ps;
		x2 *= ps;
		y2 *= ps;
		x3 *= ps;
		y3 *= ps;
		
		x1 += ps / 2;
		y1 += ps / 2;
		x2 += ps / 2;
		y2 += ps / 2;
		x3 += ps / 2;
		y3 += ps / 2;
		
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(ps));
		
	    TriangleShape triangleShape = new TriangleShape(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3));
	    g.draw(triangleShape);
	    
		g.setStroke(oldStroke);
	}
	
	private void fillTriangleA(Graphics2D g, int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
		g.setColor(color);
		
		int ps = GameEngine.getPixelSize();
		
		x1 *= ps;
		y1 *= ps;
		x2 *= ps;
		y2 *= ps;
		x3 *= ps;
		y3 *= ps;
		
		x1 += ps / 2;
		y1 += ps / 2;
		x2 += ps / 2;
		y2 += ps / 2;
		x3 += ps / 2;
		y3 += ps / 2;
		
	    TriangleShape triangleShape = new TriangleShape(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2), new Point2D.Double(x3, y3));
	    g.fill(triangleShape);
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
