/**
 * Mockit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * Mockit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with Mockit. If not, see <http://www.gnu.org/licenses/>.
 */

package cn.thinkinginjava.data.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a Mockit service registry.
 * This class implements the Serializable interface to support serialization.
 */
@Data
public class DatasetDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    private String id;

    private String datasourceId;

    private String name;

    private String script;

    private Integer enabled;

    private Integer valid;

    private String remarks;

    private Date createAt;

    private Date updateAt;

}
