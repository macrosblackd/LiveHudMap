package org.gotti.wurmonline.clientmods.livehudmap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javassist.*;
import javassist.bytecode.Descriptor;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.StackMap;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.classhooks.InvocationHandlerFactory;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmMod;

import com.wurmonline.client.game.World;
import com.wurmonline.client.renderer.gui.HeadsUpDisplay;
import com.wurmonline.client.renderer.gui.LiveMapWindow;
import com.wurmonline.client.renderer.gui.MainMenu;
import com.wurmonline.client.renderer.gui.WurmComponent;
import com.wurmonline.client.settings.SavePosManager;
import sun.security.krb5.internal.crypto.Des;

public class LiveHudMapMod implements WurmMod, Initable, PreInitable {

	private Object liveMap;

	@Override
	public void preInit() {
	}

	@Override
	public void init() {

		// com.wurmonline.client.renderer.gui.HeadsUpDisplay.init(int, int)
		HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "init", "(II)V",
				new InvocationHandlerFactory() {

					@Override
					public InvocationHandler createInvocationHandler() {
						return new InvocationHandler() {

							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								method.invoke(proxy, args);

								initLiveMap((HeadsUpDisplay) proxy);

								return null;
							}
						};
					}
				});

		HookManager.getInstance().registerHook("com.wurmonline.client.console.WurmConsole", "handleInput2", "(Ljava/lang/String;Z)V",
				new InvocationHandlerFactory() {

					@Override
					public InvocationHandler createInvocationHandler() {
						return new InvocationHandler() {

							@Override
							public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
								synchronized (proxy) {
									String string = String.valueOf(args[0]);
									if (string.startsWith("toggle livemap") && liveMap instanceof LiveMapWindow) {
										((LiveMapWindow)liveMap).toggle();
										return null;
									}

									return method.invoke(proxy, args);
								}
							}
						};
					}
				});



		try {
			ClassPool classPool = HookManager.getInstance().getClassPool();
			CtClass serverConnectionListener = classPool.get("com.wurmonline.client.comm.ServerConnectionListenerClass");

			CtClass[] paramTypes = {
					CtPrimitiveType.longType,
					classPool.get("java.lang.String"),
					classPool.get("java.lang.String"),
					CtClass.byteType,
					CtClass.floatType,
					CtClass.floatType,
					CtClass.floatType,
					CtClass.floatType,
					CtClass.byteType,
					classPool.get("java.lang.String"),
					CtClass.shortType,
					CtClass.floatType,
					CtClass.longType,
					CtClass.byteType
			};

            ArrayList<String> itemsToLookFor = new ArrayList<String>();

            itemsToLookFor.add("treasure");
            itemsToLookFor.add("source");

			CtMethod addItem = serverConnectionListener.getMethod("addItem", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes));
			HookManager.getInstance().registerHook("com.wurmonline.client.comm.ServerConnectionListenerClass", "addItem", Descriptor.ofMethod(CtPrimitiveType.voidType, paramTypes),
					new InvocationHandlerFactory() {

						@Override
						public InvocationHandler createInvocationHandler() {
							return new InvocationHandler() {
								@Override
								public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
									method.invoke(proxy, args);

                                    String name = (String)args[2];

                                    boolean found = false;
                                    for(String itemName : itemsToLookFor) {
                                        found = name.matches(".*"+itemName+".*");
                                        if(found) break;
                                    }



                                    if(!found) return null;

                                    long id = (long)args[0];
									float x = (float)args[4];
									float y = (float)args[5];


                                    int layer = (byte)args[8];

                                    appendToFile(String.format("Found %s @ X:%f Y:%f", name, x, y));

									if(liveMap instanceof LiveMapWindow) {
										((LiveMapWindow)liveMap).addGroundItem(id, name, x, y, layer);
									}

                                    return null;
								}
							};
						}
					});


		}catch (NotFoundException e) {
			throw new HookException(e);
		}
	}

	public static void appendToFile(String msg) {
		try {
			FileWriter fstream = new FileWriter("items.txt", true);
			BufferedWriter out = new BufferedWriter(fstream);
			PrintWriter pWriter = new PrintWriter(out, true);
			pWriter.println(msg);
			pWriter.close();
		}
		catch (Exception ie) {
			throw new RuntimeException("Could not write items to file", ie);
		}
	}
	
	private void initLiveMap(HeadsUpDisplay hud) {

		try {
			World world = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "world"));

			LiveMapWindow liveMapWindow = new LiveMapWindow(world);
			this.liveMap = liveMapWindow;

			MainMenu mainMenu = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "mainMenu"));
			mainMenu.registerComponent("Live map", liveMapWindow);

			List<WurmComponent> components = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "components"));
			components.add(liveMapWindow);
			
			SavePosManager savePosManager = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(hud.getClass(), "savePosManager"));
			savePosManager.registerAndRefresh(liveMapWindow, "livemapwindow");
		}
		catch (IllegalArgumentException | IllegalAccessException | ClassCastException | NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

}
