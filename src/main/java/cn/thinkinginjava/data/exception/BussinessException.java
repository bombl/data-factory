/**
 *
 * Mockit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mockit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mockit. If not, see <http://www.gnu.org/licenses/>.
 */

package cn.thinkinginjava.data.exception;

/**
 * Bussiness Exception.
 */
public class BussinessException extends RuntimeException {

    private static final long serialVersionUID = 8068509879445395356L;

    /**
     * Instantiates a new Mockit exception.
     *
     * @param e the e
     */
    public BussinessException(final Throwable e) {
        super(e);
    }

    /**
     * Instantiates a new Mockit exception.
     *
     * @param message the message
     */
    public BussinessException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Mockit exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public BussinessException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}