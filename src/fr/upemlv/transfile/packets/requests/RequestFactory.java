package fr.upemlv.transfile.packets.requests;

import fr.upemlv.transfile.enums.RequestsEnum;
import fr.upemlv.transfile.packets.Requests;

/**
 * 
 * Represents a Factory of Requests
 * 
 * @author DEBEAUPUIS Gabriel & FOUCAULT Jeremy
 * 
 */
public class RequestFactory {

	/**
	 * Static method granting the ability to get a new Instance of the correct
	 * Request depending on the RequestEnum give in parameter, as well as all
	 * the arguments.
	 * 
	 * @param req
	 *            The RequestsEnum
	 * @param arguments
	 *            the arguments
	 * @return a new Instance of the correct Request
	 */
	public static Requests createRequest(RequestsEnum req, String[] arguments) {

		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < arguments.length; i++) {
			sb.append(arguments[i]);
			if ((i + 1) != arguments.length)
				sb.append(" ");
		}
		String arg = sb.toString();
		if (req.equals(RequestsEnum.CD)) {
			if (arg.equals("")) {
				throw new IllegalArgumentException(
						"The CD request contains the wrong number of arguments");
			}

			return new RqCd(arg);

		} else if (req.equals(RequestsEnum.LS)) {
			if (!arg.equals("")) {
				throw new IllegalArgumentException("Too many arguments");
			}

			return new RqLs();

		} 
		
		else if (req.equals(RequestsEnum.KILL)) {
			if (arg.equals("")) {
				throw new IllegalArgumentException(
						"The KILL request contains the wrong number of arguments");
			}

			return new RqKill(Integer.parseInt(arg));

		}

		else if (req.equals(RequestsEnum.GET)) {
			if (arg.equals("")) {
				throw new IllegalArgumentException(
						"The GET request contains the wrong number of arguments");
			}
			return new RqGet(arg);

		}

		else if (req.equals(RequestsEnum.EXIT)) {
			if (!arg.equals("")) {
				throw new IllegalArgumentException("too many arguments");
			}
			return new RqExit();
		}

		return null;
	}

}
