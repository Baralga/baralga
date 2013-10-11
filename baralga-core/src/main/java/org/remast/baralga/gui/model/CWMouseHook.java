package org.remast.baralga.gui.model;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.MSG;
import com.sun.jna.platform.win32.WinUser.POINT;

public class CWMouseHook {
	public final User32 USER32INST;
	public final Kernel32 KERNEL32INST;

	PresentationModel model;

	public CWMouseHook(PresentationModel model) {
		this.model = model;
		
		if (!Platform.isWindows()) {
			throw new UnsupportedOperationException(
					"Not supported on this platform.");
		}
		
		USER32INST = User32.INSTANCE;
		KERNEL32INST = Kernel32.INSTANCE;
		mouseHook = hookTheMouse();
		Native.setProtected(true);

	}

	public static LowLevelMouseProc mouseHook;
	public HHOOK hhk;
	public Thread thrd;
	public boolean threadFinish = true;
	public boolean isHooked = false;
	public static final int WM_MOUSEMOVE = 512;
	public static final int WM_LBUTTONDOWN = 513;
	public static final int WM_LBUTTONUP = 514;
	public static final int WM_RBUTTONDOWN = 516;
	public static final int WM_RBUTTONUP = 517;
	public static final int WM_MBUTTONDOWN = 519;
	public static final int WM_MBUTTONUP = 520;

	public void unsetMouseHook() {
		threadFinish = true;
		if (thrd.isAlive()) {
			thrd.interrupt();
			thrd = null;
		}
		isHooked = false;
	}

	public boolean isIsHooked() {
		return isHooked;
	}

	public void setMouseHook() {
		thrd = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (!isHooked) {
						hhk = USER32INST.SetWindowsHookEx(14, mouseHook, KERNEL32INST.GetModuleHandle(null), 0);
						isHooked = true;
						MSG msg = new MSG();
						while ((USER32INST.GetMessage(msg, null, 0, 0)) != 0) {
							USER32INST.TranslateMessage(msg);
							USER32INST.DispatchMessage(msg);
							System.out.print(isHooked);
							if (!isHooked)
								break;
						}
					} else
						System.out.println("The Hook is already installed.");
				} catch (Exception e) {
					System.err.println(e.getMessage());
					System.err.println("Caught exception in MouseHook!");
				}
			}
		}, "Named thread");
		threadFinish = false;
		thrd.start();

	}

	private interface LowLevelMouseProc extends HOOKPROC {
		LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT lParam);
	}

	public LowLevelMouseProc hookTheMouse() {
		return new LowLevelMouseProc() {
			@Override
			public LRESULT callback(int nCode, WPARAM wParam, MOUSEHOOKSTRUCT info) {
				if (nCode >= 0) {
					switch (wParam.intValue()) {
					case CWMouseHook.WM_LBUTTONDOWN:
					case CWMouseHook.WM_RBUTTONDOWN:
					case CWMouseHook.WM_MBUTTONDOWN:
					case CWMouseHook.WM_LBUTTONUP:
					case CWMouseHook.WM_MOUSEMOVE:
						model.addUserActivity();
						break;
					default:
						break;
					}
					/**************************** DO NOT CHANGE, this code unhooks mouse *********************************/
					if (threadFinish == true) {
						USER32INST.PostQuitMessage(0);
					}
					/*************************** END OF UNCHANGABLE *******************************************************/
				}
				return USER32INST.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
			}
		};
	}

	public class Point extends Structure {
		public class ByReference extends Point implements Structure.ByReference {
		};

		public NativeLong x;
		public NativeLong y;
	}

	public static class MOUSEHOOKSTRUCT extends Structure {
		public static class ByReference extends MOUSEHOOKSTRUCT implements Structure.ByReference {
		};

		public POINT pt;
		public HWND hwnd;
		public int wHitTestCode;
		public ULONG_PTR dwExtraInfo;
	}
}
