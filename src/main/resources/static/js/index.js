function generateData() {
    const ddlMap = new Map();
    // 遍历每个tab
    $('#myTabs1 div').each(function() {
        // 获取tab名
        const tabName = $(this).text();
        ddlMap.set(tabName,$(`#${tabName}-1 textarea`).val());
    });

    // event.preventDefault(); // 阻止默认行为，即表单提交
    var obj = {};
    obj.id = $('#selectServiceName').val();
    obj.generateNum = $('#generateNum').val();
    obj.querySql = $("#sqlTextarea").val();
    const dataMap = getCondition();
    obj.extWhereMap = Object.fromEntries(dataMap);
    obj.ddlMap = Object.fromEntries(ddlMap);
    // obj.ddlList = arr;
    var data = JSON.stringify(obj);

    $.ajax({
        url: base_url + "/generateData",
        type: "POST",
        data: data,
        contentType: "application/json",
        success: function (response) {
            handleApiResponse(response.data);
            // alert(response.data);
        },
        error: function (xhr, status, error) {
            // 处理错误
            // toastr.error("操作失败");
        }
    });
}

function getCondition() {
    // 创建一个空 Map，用于存储数据
    const dataMap = new Map();

    // 遍历每个标签页
    $('#tabs .nav-link').each(function () {
        const tabId = $(this).attr('href').substring(1); // 获取标签页的ID

        const tabContent = $(`#${tabId} select option:selected`).filter(function () {
            // 筛选出不是 "请选择" 且值不为空的选项
            return $(this).val() !== '' && $(this).text() !== '请选择';
        });

        // 遍历符合条件的选项，并将数据存储到 Map 中
        tabContent.each(function () {
            const expression = $(this).val(); // 获取选项的值（例如：">", "=", "!=" 等）
            const fieldName = $(this).closest('tr').find('td:first-child').text(); // 获取字段名称
            let inputValue = '';

            // 获取输入框的值，如果是日期时间选择器，则获取它的值方式不同
            if ($(this).closest('tr').find('input[type="datetime-local"]').length) {
                inputValue = $(this).closest('tr').find('input[type="datetime-local"]').val().replace("T", " ");
            } else if ($(this).closest('tr').find('input[type="text"]').length){
                inputValue = $(this).closest('tr').find('input[type="text"]').val();
            } else {
                inputValue = $(this).closest('tr').find('select').val();
            }

            if (expression === 'in') {
                if (inputValue.includes(',')) {
                    const values = inputValue.split(',').map(value => `'${value.trim()}'`);
                    inputValue = `IN(${values.join(', ')})`;
                } else {
                    // 否则，直接使用原始的 inputValue
                    inputValue = `IN('${inputValue}')`;
                }
            }

            if (expression === 'not in') {
                if (inputValue.includes(',')) {
                    const values = inputValue.split(',').map(value => `'${value.trim()}'`);
                    inputValue = `NOT IN(${values.join(', ')})`;
                } else {
                    // 否则，直接使用原始的 inputValue
                    inputValue = `NOT IN('${inputValue}')`;
                }
            }

            if (expression !== 'in' && expression !== 'not in' && expression !== '=') {
                inputValue = expression + `'${inputValue}'`;
            }

            // 构建 Map 的键，格式为：tab名-字段名
            const key = `${tabId}-${fieldName}`;

            // 存储数据到 Map 中
            dataMap.set(key, inputValue);
        });
    });

    // 现在 dataMap 中包含了数据，每个键为 tab名-字段名，值为对应的 value
    console.log(dataMap);
    return dataMap;
}

function showDdlTextArea(tableNames) {
    // 每行显示的textarea数量
    const textareasPerRow = 1;

    // 获取textarea容器元素
    const textareaContainer = document.getElementById("textareaContainer");
    while (textareaContainer.firstChild) {
        textareaContainer.removeChild(textareaContainer.firstChild);
    }
    let currentRowDiv = null; // 用于跟踪当前行的div元素

    // 遍历表名列表并生成textarea
    tableNames.forEach((tableName, index) => {
        if (index % textareasPerRow === 0) {
            // 创建一个新的div元素作为新的行
            currentRowDiv = document.createElement("div");
            currentRowDiv.classList.add("col-sm-6");
            textareaContainer.appendChild(currentRowDiv);
        }

        // 创建一个新的div元素作为每个textarea的容器
        const textareaDiv = document.createElement("div");
        textareaDiv.classList.add("form-group"); // 控制每个textarea所占宽度
        currentRowDiv.appendChild(textareaDiv);

        // 创建一个新的textarea元素
        const textarea = document.createElement("textarea");
        textarea.classList.add("form-control");
        textarea.rows = 3; // 设置行数
        textarea.placeholder = `请输入${tableName}的表结构`; // 使用表名作为placeholder
        textareaDiv.appendChild(textarea);
    });
}

function createTab(tabName, fieldNames, fieldValues, insertStatements) {
    // 创建选项卡和表格容器
    const tabId = `tab-${tabName}`;
    const tabContentId = `tab-content-${tabName}`;
    const tabPane = `<li><a  class="nav-link" href="#${tabContentId}" data-toggle="tab">${tabName}</a></li>`;
    const tabContent = `<div class="tab-pane" id="${tabContentId}">
                            <div class="container">
                                <div class="box">
                                    <div class="box-body">
                                        <table class="table table-bordered table-striped" id="${tabId}">
                                            <thead>
                                                <tr id="table-headers-${tabName}">
                                                    <!-- 表头将在 JavaScript 中填充 -->
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <!-- 表格数据将在 JavaScript 中填充 -->
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>`;

    // 添加选项卡和表格容器到页面
    $('#myTabs').append(tabPane);
    $('#tab-content').append(tabContent);

    // 填充表头
    var tableHeaders = fieldNames.map(function (fieldName) {
        return '<th>' + fieldName + '</th>';
    });
    $(`#table-headers-${tabName}`).html(tableHeaders.join(''));

    // 创建 DataTable 列配置
    var columns = fieldNames.map(function (fieldName) {
        return {data: fieldName};
    });
    const activeTab = $(`#${tabId}`);
    activeTab.data('fieldValues', insertStatements);
    // 初始化 DataTables
    $(`#${tabId}`).DataTable({
        data: fieldValues,
        columns: columns,
    });

    // 激活第一个选项卡
    if ($('#myTabs li').length === 1) {
        $('#myTabs li').addClass('active');
        $('#myTabs li a').addClass('active');
        $(`#${tabContentId}`).addClass('active');
    }

    // 添加点击事件处理程序以处理选项卡的切换
    $(`#${tabId}`).on('click', function () {
        // 添加新选项卡的样式
        $(`#${tabId}`).addClass('active');
        $(`#${tabContentId}`).addClass('active');
    });
}


function handleApiResponse(response) {
    // 清空所有选项卡和内容
    $('#myTabs').empty();
    $('#tab-content').empty();
    // 遍历response的键（表名）
    for (const tableName of Object.keys(response)) {
        const insertStatements = response[tableName];

        // 解析INSERT语句以获取字段名和字段值
        const {fieldNames, fieldValues} = parseFieldNamesAndValuesFromInsertStatement(insertStatements[0]);

        // 将一维数组转换为多个对象的数组
        var fieldValues2 = [];
        for (const insertStatement of insertStatements) {
            const {fieldValues} = parseFieldNamesAndValuesFromInsertStatement(insertStatement);
            for (var i = 0; i < fieldValues.length; i += fieldNames.length) {
                var row = {};
                for (var j = 0; j < fieldNames.length; j++) {
                    row[fieldNames[j]] = fieldValues[i + j];
                }
                fieldValues2.push(row);
            }
        }
        // 创建选项卡并渲染表格
        createTab(tableName, fieldNames, fieldValues2, insertStatements);
    }
    const buttons = `<div class="ml-auto" style="margin-top: 6px;">
            <button type="button" class="btn btn-primary btn-sm" id="copyValue">复制</button>
            <button type="button" class="btn btn-danger btn-sm ml-2" id="execute">执行</button>
        </div>`;
    $('#myTabs').append(buttons);
    const copyValue = document.getElementById('copyValue');
    copyValue.addEventListener('click', function (event) {
        // 获取激活的选项卡的名称（可以根据您的需求进行更改）
        const activeTabName = $('#myTabs .nav-link.active').html();

        // 使用选项卡名称构建激活选项卡的ID
        const activeTabId = `tab-${activeTabName}`;

        // 使用数据属性获取 fieldValues
        const fieldValues = $(`#${activeTabId}`).data('fieldValues');

        copyArrayToClipboard(fieldValues);
        var Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 2000
        });
        Toast.fire({
            icon: 'success',
            title: '已复制到剪切板.'
        })
    });
    const execute = document.getElementById('execute');
    execute.addEventListener('click', function (event) {
        // 获取激活的选项卡的名称（可以根据您的需求进行更改）
        const activeTabName = $('.nav-link.active').html();

        // 使用选项卡名称构建激活选项卡的ID
        const activeTabId = `tab-${activeTabName}`;

        // 使用数据属性获取 fieldValues
        const fieldValues = $(`#${activeTabId}`).data('fieldValues');

        var obj = {};
        obj.sqlList = fieldValues;
        obj.id = $('#selectServiceName').val();
        var data = JSON.stringify(obj);

        $.ajax({
            url: base_url + "/saveData",
            type: "POST",
            data: data,
            contentType: "application/json",
            success: function (response) {
                var Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 2000
                });
                Toast.fire({
                    icon: 'success',
                    title: '执行成功.'
                })
            },
            error: function (xhr, status, error) {
                // 处理错误
                // toastr.error("操作失败");
            }
        });
    });
}

function copyArrayToClipboard(arr) {
    const textToCopy = arr.join('\n'); // 将数组元素用换行符连接成字符串
    const textarea = document.createElement('textarea');
    textarea.value = textToCopy;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand('copy');
    document.body.removeChild(textarea);
}


function parseFieldNamesAndValuesFromInsertStatement(insertStatement) {
    insertStatement = insertStatement.replace(/TO_DATE\('([^']*)', 'YYYY-MM-DD HH24:MI:SS'\)/g, '$1');
    insertStatement = insertStatement.replace(/'/g, "");

    // 修复正则表达式来匹配 INSERT 语句的字段和值
    const matches = insertStatement.match(/INSERT INTO \w+ \(([^)]+)\) VALUES \(([^;]+)\);/);

    if (matches && matches.length === 3) {
        const fieldList = matches[1];
        const valueList = matches[2];
        const fieldNames = fieldList.split(', ');
        const fieldValues = valueList.split(', ');
        return {fieldNames, fieldValues};
    }
}

function showConfirmationModal(event) {
    event.preventDefault();
    // $('#confirmationModal').modal('show');

    // 获取 Control Sidebar 元素
    const controlSidebar = document.querySelector('.control-sidebar');
    // 获取页面主体内容区域
    const pageContent = document.getElementById('closeSidebar');
    // 添加点击事件监听器到页面主体内容区域
    pageContent.addEventListener('click', function (event) {
        // 检查点击事件是否发生在 Control Sidebar 之外的区域
        // if (!controlSidebar.contains(event.target)) {
        //     // 提示是否关闭侧边栏
        //     $('#confirmationModal').modal('show');
        // }
        $('#confirmationModal').modal('hide');
        controlSidebar.style.display = 'none';
    });
    const confirmCloseSidebar = document.getElementById('confirmCloseSidebar');

    confirmCloseSidebar.addEventListener('click', function (event) {
        $('#confirmationModal').modal('hide');
        controlSidebar.style.display = 'none';
    });
}

function changeDdl(button) {
    const key = button.innerHTML;
    // 隐藏所有内容
    $('#tab-content1 .tab-pane').hide();
    // 移除其他tab的激活状态
    $('#myTabs1 div').removeClass('active');
    // 激活当前tab
    $(button).addClass('active');
    // 获取当前选项卡对应的内容id
    const tabId = $(button).find('div').attr('id');
    // 显示对应的内容
    $(`#${tabId}-1`).show();
    const tabContentId = `${key}-1`;
    $(`#${tabContentId}`).addClass('active').show();
}
function setCondition(btn) {

    const ddlMap = new Map();
    // 遍历每个tab
    $('#myTabs1 div').each(function() {
        // 获取tab名
        const tabName = $(this).text();
        let ddl = $(`#${tabName}-1 textarea`).val();
        ddlMap.set(tabName,ddl);
    });

    var obj = {};
    obj.id = $('#selectServiceName').val();
    obj.querySql = $("#sqlTextarea").val();
    obj.ddlMap = Object.fromEntries(ddlMap);
    // obj.ddlList = arr;
    var data = JSON.stringify(obj);

    $.ajax({
        url: base_url + "/parseSqlDefine",
        type: "POST",
        data: data,
        contentType: "application/json",
        success: function (response) {
            // 清空现有的标签页和内容
            $('#tabs').empty();
            $('#tabContent').empty();

            if (!btn) {
                $('#myTabs1').empty();
                $('#tab-content1').empty();
            }
            // 假设 response.data 是你的数据对象，包含了标签页的信息和字段定义
            for (const [key, columnDefinitions] of Object.entries(response.data)) {
                if (!btn) {
                    // 创建一个新选项卡内容
                    const tabContent = `
                    <div class="tab-pane" id="${key}-1" role="tabpanel" aria-labelledby="${key}-tab1" style="display: none;">
                        <!-- 在这里生成表结构输入区域 -->
                        <textarea class="form-control" id="${key}-field1" onchange="setCondition(this)">${columnDefinitions.length > 0 ? columnDefinitions[0].ddl : ''}</textarea>
                    </div>`;
                    $('#tab-content1').append(tabContent);

                    $('#myTabs1').append(`
                        <li class="nav-item">
                            <div class="nav-link inactive-tab" id="${key}-tab1" role="tab1" onclick="changeDdl(this)">${key}</div>
                        </li>
                    `);

                    $('#myTabs1 div:first').addClass('active');
                    $('#tab-content1 div:first').addClass('active').show();
                }
                // 创建标签页按钮
                $('#tabs').append(`
                    <li class="nav-item">
                        <a class="nav-link" id="${key}-tab" data-toggle="tab" href="#${key}" role="tab">${key}</a>
                    </li>
                `);
                // 创建标签页内容容器
                $('#tabContent').append(`
                <div class="tab-pane" id="${key}" role="tabpanel">
                    <div class="table-container">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>字段</th>
                                    <th>字段类型</th>
                                    <th>表达式</th>
                                    <th>值</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${columnDefinitions.length > 0 ? columnDefinitions.map(column => `
                                    <tr>
                                        <td>${column.columnName}</td>
                                        <td>${column.colDataType.dataType}${column.colDataType.argumentsStringList === null ? '' : `(${column.colDataType.argumentsStringList})`}</td>
                                        <td>
                                            <select class="columnOption">
                                                <option value="">请选择</option> <!-- 默认的 "请选择" 选项 -->
                                                <option ${column.option === '>' ? 'selected' : ''}>></option>
                                                <option ${column.option === '=' ? 'selected' : ''}>=</option>
                                                <option ${column.option === '!=' ? 'selected' : ''}>!=</option>
                                                <option ${column.option === '<' ? 'selected' : ''}><</option>
                                                <option ${column.option === 'IN' ? 'selected' : ''}>in</option>
                                                <option ${column.option === 'NOT IN' ? 'selected' : ''}>not in</option>
                                                <option ${column.option === 'LIKE' ? 'selected' : ''}>like</option>
                                                <option value="DATASET">数据集</option>
                                            </select>
                                        </td>
                                        <td>
                                            ${column.colDataType.dataType === 'DATE' || column.colDataType.dataType === 'date'?
                                                `<input type="datetime-local" value="${column.value !== null ? column.value.replace(' ', 'T') : ''}">` :
                                                (column.value !== null ? `<input type="text" value="${column.value}">` : '<input type="text" value="">')}
                                        </td>
                                    </tr>
                                    </tr>
                                `).join('') : ''}
                            </tbody>
                        </table>
                    </div>
                </div>
            `);
            }

            // 激活第一个标签页
            $('#tabs a:first').tab('show');

            $('.columnOption').on('change', function () {
                const selectedOption = $(this).val(); // 获取当前选择的选项的值
                const tdElement = $(this).closest('td');
                const nextTdElement = tdElement.next('td');
                if (selectedOption === 'DATASET') {
                    // 用户选择了数据集，此时可以从后台加载数据
                    loadDatasetData(nextTdElement);
                }
            });

        },
        error: function (xhr, status, error) {
            // 处理错误
            // toastr.error("操作失败");
        }
    });
}

// 生成下拉框的选项，处理空选项
function generateOptions(option) {
    if (!option || option.trim() === '') {
        return '<option></option>';
    }

    const optionValues = option.split(','); // 假设 option 的值以逗号分隔
    return optionValues.map(value => `<option>${value}</option>`).join('');
}

function loadDatasetData(tdElement) {
    // const key = selectElement.closest('.tab-pane').attr('id'); // 获取当前标签页的 ID
    // const columnName = selectElement.closest('tr').find('td:first').text(); // 获取当前行的第一个单元格的文本
    // const targetSelect = $(`select[data-key="${key}"][data-column-name="${columnName}"]`);

    var obj = {};
    obj.currentPage = 1;
    obj.pageSize = 200;
    var data = JSON.stringify(obj);

    $.ajax({
        url: base_url + "/dataset/list",
        type: "post",
        data: data,
        contentType: "application/json",
        success: function (response) {
            // 清空下拉框选项
            tdElement.empty();

            if (response.data.length === 0) {
                return;
            }

            const selectElement = $('<select class="columnOption" style="width: 159px;height: 29px;"></select>');
            // selectElement.append('<option value="">请选择</option>'); // 默认的 "请选择" 选项

            response.data.forEach(function (item) {
                selectElement.append($('<option></option>').val(item.id).text(item.name));
            });

            // 将下拉框添加到<td>中
            tdElement.append(selectElement);
        },
        error: function (xhr, status, error) {
            // 处理错误
            console.error('Error loading data:', error);
        }
    });
}

$(document).ready(function () {
    $('.columnOption').on('change', function () {
        const selectedOption = $(this).val(); // 获取当前选择的选项的值

        if (selectedOption === 'DATASET') {
            // 用户选择了数据集，此时可以从后台加载数据
            loadDatasetData();
        }
    });

    var obj = {};
    obj.currentPage = 1;
    obj.pageSize = 20;
    var data = JSON.stringify(obj);

    $.ajax({
        url: base_url + "/list",
        type: "post",
        data: data,
        contentType: "application/json",
        success: function (response) {
            // 清空下拉框选项
            $('#selectServiceName').empty();

            if (response.data.length === 0) {
                return;
            }
            // 填充下拉框选项
            response.data.forEach(function (item) {
                var option = $('<option></option>').attr('value', item.id).text(item.name);
                $('#selectServiceName').append(option);
            });
            const st = document.getElementById('sqlTextarea');
            let previousValue = st.value; // 用于存储之前的文本内容
            if (previousValue !== '') {
                setCondition(null);
            }
        },
        error: function () {
            // 处理请求错误
            console.log('请求后台接口失败');
        }
    });

    // 获取 Control Sidebar 元素
    const controlSidebar = document.querySelector('.control-sidebar');

    // 获取 Control Sidebar 元素
    // const sidebarbtn = document.getElementById('sidebarbtn');
    // sidebarbtn.addEventListener('click', function (event) {
    //     setCondition();
    // });

    // 获取页面主体内容区域
    const pageContent = document.querySelector('.content-wrapper');

    // 添加点击事件监听器到页面主体内容区域
    pageContent.addEventListener('click', function (event) {
        // 检查点击事件是否发生在 Control Sidebar 之外的区域
        // if (!controlSidebar.contains(event.target) && getComputedStyle(controlSidebar).display === 'block') {
        //     // 提示是否关闭侧边栏
        //     $('#confirmationModal').modal('show');
        //
        //     const confirmCloseSidebar = document.getElementById('confirmCloseSidebar');
        //
        //     confirmCloseSidebar.addEventListener('click', function (event) {
        //         $('#confirmationModal').modal('hide');
        //         controlSidebar.style.display = 'none';
        //     });
        // }
        $('#confirmationModal').modal('hide');
        controlSidebar.style.display = 'none';
    });

    // 获取解析按钮元素
    // const formatButton = document.getElementById('formatButton');

    // 监听解析按钮的点击事件
    // formatButton.addEventListener('click', function (event) {
    //     event.preventDefault(); // 阻止默认行为，即表单提交
    //     var obj = {};
    //     obj.querySql = $("#sqlTextarea").val();
    //     var data = JSON.stringify(obj);
    //
    //     $.ajax({
    //         url: base_url + "/queryTableNames",
    //         type: "POST",
    //         data: data,
    //         contentType: "application/json",
    //         success: function (response) {
    //             showDdlTextArea(response.data);
    //         },
    //         error: function (xhr, status, error) {
    //             // 处理错误
    //             // toastr.error("操作失败");
    //         }
    //     });
    // });

    const st = document.getElementById('sqlTextarea');
    // 选择 SQL 文本区域元素
    const sqlTextarea = $('#sqlTextarea');
    let previousValue = st.value; // 用于存储之前的文本内容
    let currentValue = st.value; // 用于存储之前的文本内容

    // 监听输入事件（文本内容变化）
    st.addEventListener('input', function () {
        let nowValue = sqlTextarea.val();

        // 检查文本内容是否有变动
        if (currentValue !== nowValue) {
            // 文本内容发生变化时触发的操作
            console.log('文本内容变化:', currentValue);
            currentValue = nowValue; // 更新 previousValue
        }
    });

    // 监听失去焦点事件
    st.addEventListener('blur', function () {
        // 在失去焦点时触发的操作
        if (currentValue !== previousValue) {
            // 文本内容发生变化时触发 setCondition()
            setCondition(null);
            previousValue = currentValue;
        }
    });
});
