function saveDatasource(event) {
    event.preventDefault();
    var obj = {};
    obj.id = $("#id").val();
    obj.datasourceId = $('#selectServiceName').val();
    obj.name = $("#name").val();
    obj.script = $("#script").val();
    obj.remarks = $("#remarks").val();
    // obj.ddlList = arr;
    var data = JSON.stringify(obj);

    $.ajax({
        url: base_url + "/dataset/saveOrUpdate",
        type: "POST",
        data: data,
        contentType: "application/json",
        success: function (response) {
            // 获取 Control Sidebar 元素
            const controlSidebar = document.querySelector('.control-sidebar');
            $('#confirmationModal').modal('hide');
            controlSidebar.style.display = 'none';
            refreshTableData();

        },
        error: function (xhr, status, error) {
            // 处理错误
            // toastr.error("操作失败");
        }
    });
}

// 刷新表格数据
function refreshTableData() {
    var table = $('#example2').DataTable();

    // Clear existing table data
    table.clear().draw();
}

function searchTableData(event) {
    event.preventDefault();
    var serviceNameElement = document.getElementById("selectServiceName0");
    var serviceId = serviceNameElement.options[serviceNameElement.selectedIndex].value;
    var className = $('#className').val().trim();
    var enabled = $('#enabled').val();
    var searchCondition = {};

    if (serviceId) {
        searchCondition.serviceId = serviceId;
    }
    if (className) {
        searchCondition.className = className;
    }
    if (enabled) {
        searchCondition.enabled = enabled;
    }
    table.search(JSON.stringify(searchCondition)).draw();
}

function checkItem(checkbox) {
    var isChecked = checkbox.checked;
    var checkboxes = document.querySelectorAll('input[name="userState"]');

    checkboxes.forEach(function (item) {
        if (isChecked) {
            item.checked = true;
        } else {
            item.checked = false;
        }
    });
};

function toLowerFirstLetter(str) {
    return str.charAt(0).toLowerCase() + str.slice(1);
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

function editData(button) {
    var row = $(button).closest('tr'); // 获取当前按钮所在的行
    var rowData = table.row(row).data(); // 使用DataTables API获取行数据

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
                if (rowData.datasourceId === item.id) {
                    var option = $('<option selected></option>').attr('value', item.id).text(item.name);
                    $('#selectServiceName').append(option);
                } else {
                    var option = $('<option></option>').attr('value', item.id).text(item.name);
                    $('#selectServiceName').append(option);
                }
            });
        },
        error: function () {
            // 处理请求错误
            console.log('请求后台接口失败');
        }
    });

    // 将数据填充到侧边栏中的输入字段中
    document.getElementById('name').value = rowData.name;
    document.getElementById('script').value = rowData.script;
    document.getElementById('selectServiceName').value = rowData.datasourceId;
    document.getElementById('remarks').value = rowData.remarks;
    document.getElementById('id').value = rowData.id;
}

function addData(button) {
    // 将数据填充到侧边栏中的输入字段中
    document.getElementById('name').value = "";
    document.getElementById('script').value = "";
    document.getElementById('remarks').value = "";
    document.getElementById('id').value = "";
}

function del(button) {
    var row = $(button).closest('tr'); // 获取当前按钮所在的行
    var rowData = table.row(row).data(); // 使用DataTables API获取行数据
    $("#confirmDeleteModal").modal('show');

    const delButton = document.getElementById('delButton');

    delButton.addEventListener('click', function (event) {
        $('#confirmDeleteModal').modal('hide');
        var obj = {};
        obj.id = rowData.id;
        var data = JSON.stringify(obj);

        setTimeout(function () {
            $.ajax({
                url: base_url + "/dataset/delete",
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
                        title: '操作成功！'
                    });
                    refreshTableData();
                },
                error: function (xhr, status, error) {
                    // 处理错误
                    // toastr.error("操作失败");
                }
            });
        }, 300);
    });
}

$(document).ready(function () {
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
        },
        error: function () {
            // 处理请求错误
            console.log('请求后台接口失败');
        }
    });

    // 获取 Control Sidebar 元素
    const controlSidebar = document.querySelector('.control-sidebar');

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

    table = $('#example2').DataTable({
        "autoWidth": true,
        "scrollX": true,
        "scrollCollapse": true,
        fixedColumns: {
            left: 2,
            right: 1
        },
        scrollY: true,
        "columnDefs": [
            {
                "targets": "_all", // Apply to all columns
                "className": "text-center" // Center align the content
            },
            {
                "targets": 3, // Index of the "类名" column (zero-based)
                "width": "120px",
                "render": function (data, type, row) {
                    const maxChars = 30;

                    if (data && data.length > maxChars) {
                        const lastIndex = data.lastIndexOf('.');
                        if (lastIndex > maxChars - 3) {
                            const truncatedData = data.substr(lastIndex + 1);
                            return '<span title="' + data + '">' + truncatedData + '</span>';
                        } else {
                            const truncatedData = data.substr(0, maxChars - 3) + '...';
                            return '<span title="' + data + '">' + truncatedData + '</span>';
                        }
                    } else {
                        return data;
                    }
                }
            },
            {
                "targets": 4,
                "width": "60px"
            },
            {
                "targets": 5,
                "width": "60px"
            }
        ],
        "searching": false,
        "processing": true,
        "serverSide": true,
        "ajax": {
            url: base_url + "/dataset/list",
            type: "post",
            dataType: "json",
            contentType: "application/json",
            data: function (d) {
                var obj = {};
                obj.currentPage = d.start;
                obj.pageSize = d.length;
                return JSON.stringify(obj);
            }
        },
        "initComplete": function (settings, json) {
            // Toastr初始化
            toastr.options = {
                closeButton: true,
                progressBar: false,
                preventDuplicates: true,
                positionClass: "toast-top-center",
                timeOut: 1000
            };
        },
        "columns": [{
            orderable: false,
            className: 'select-checkbox',
            targets: 0,
            checkboxes: {
                selectRow: true
            },
            data: null,
            render: function (data, type, row, meta) {
                var id = row.id;
                return '<div style="text-align: center;"><input name="userState" type="checkbox" class="minimal checkbox-toolbar" data-id="' + id + '" style="width: 20px;"></div>';
            }
        },
            {
                targets: 1,
                data: null,
                render: function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {"data": "name", "orderable": false},
            {"data": "datasourceName", "orderable": false},
            {"data": "script", "orderable": false},
            {"data": "remarks", "orderable": false},
            {"data": "createTime", "orderable": false},
            {"data": "updateTime", "orderable": false},
            {
                'sTitle': '操作',
                "orderable": false,
                data: null,
                'render': function (data, type, row) {
                    return `
                            <!-- 新增按钮 -->
<!--                            <button type="button" class="btn btn-sm btn-xs" data-widget="control-sidebar" data-slide="true" onclick="addData(this)">-->
<!--                              <i class="fas fa-plus"></i> &lt;!&ndash; 使用FontAwesome图标 &ndash;&gt;-->
<!--                            </button>-->
                            
                            <!-- 修改按钮 -->
                            <button type="button" class="btn btn-sm btn-xs" data-widget="control-sidebar" data-slide="true" onclick="editData(this)">
                              <i class="fas fa-pencil-alt"></i> <!-- 使用FontAwesome图标 -->
                            </button>
                            
                            <!-- 删除按钮 -->
                            <button type="button" class="btn btn-sm btn-xs" onclick="del(this)">
                              <i class="fas fa-trash"></i> <!-- 使用FontAwesome图标 -->
                            </button>

                                `;
                },
            }
        ],
        select: {
            style: 'multi',
            selector: 'td:first-child'
        },
        "language": //把文字变为中文
            {
                // "sProcessing": "加载中...",
                "sLengthMenu": "显示条数： _MENU_ ",
                "sZeroRecords": "没有匹配结果",
                "sInfo": "第 _PAGE_ 页 ( 总共 _PAGES_ 页，_TOTAL_ 条记录 )",
                "sInfoEmpty": "无记录",
                "sInfoFiltered": "",
                "sInfoPostFix": "",
                "sSearch": "搜索:",
                "sUrl": "",
                "sEmptyTable": "表中数据为空",
                // "sLoadingRecords": "载入中...",
                "sInfoThousands": ",",
                "oPaginate": {
                    "sPrevious": "上一页", //上一页
                    "sNext": "下一页", //下一页
                },
                select: {
                    rows: {
                        _: "%d 行已选择",
                        0: "",
                        1: "1 行已选择"
                    }
                }
            },
        'aLengthMenu': [10, 20, 30, 50], //设置每页显示记录的下拉菜单
        'ordering': false
    })
});