<!DOCTYPE html>
<html lang="en">
<head>
    <#import "./common/common.ftl" as netCommon>
    <@netCommon.commonStyle />
    <style>
        .custom-select {
            width: 100%;
        }
        .modal-dialog.custom-dialog {
            max-width: 800px;
        }

        .modal-content.custom-content {
            width: 100%;
        }
        /* Apply ellipsis to the text content */
        #example2 td span {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        #example2 th {
            text-align: center;
        }
        #example2 th {
            text-align: center;
        }
        #example2 th,
        #example2 td {
            white-space: nowrap;
        }
        .hidden-element {
            display: none;
        }
    </style>
    <title>Mockit</title>
</head>
<body class="hold-transition sidebar-mini layout-fixed">
<div class="wrapper">

    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "jobinfo" />
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <div class="content-header" style="padding-bottom: 5px;">
            <div class="container-fluid">
                <div class="row mb-2">
                    <div class="col-sm-6">
                        <h1 style="margin: 0;font-size: 24px;">数据集管理</h1>
                    </div><!-- /.col -->
                </div><!-- /.row -->
            </div><!-- /.container-fluid -->
        </div>
        <!-- /.content-header -->
        <!-- Main content -->

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-12">
                    <div class="col-xs-12">
                        <div class="box">
                            <form id="myForm" method="post" class="form-inline">
                                <div class="box-header">
                                    <div class="input-group">
                                        <a class="btn btn-primary dialog" style="margin-right: 3px;background-color: #3c8dbc;" data-widget="control-sidebar" ata-slide="true"
                                           data-title="新增" data-width="850" data-height="550" onclick="addData(this)"><i class="fa fa-check"></i>新增</a>

<#--                                        <button type="button" class="btn btn-tool" data-widget="control-sidebar" ata-slide="true">-->
<#--                                            <i class="fas fa-th-large" style="color:#007bff"></i>-->
<#--                                        </button>-->
                                    </div>
                                </div>
                            </form>
                            <div class="row" style="margin-top: 1px;">
                                <div class="col-12">
                                    <div class="card">
                                        <!-- /.card-header -->
                                        <div class="card-body">
                                            <table id="example2" class="table table-striped table-bordered table-hover" style="width:100%">
                                                <thead>
                                                <tr>
                                                    <th><input name="userState" type="checkbox" onclick="checkItem(this)" class="minimal checkbox-toolbar"></th>
                                                    <th>行号</th>
                                                    <th>名称</th>
                                                    <th>数据源</th>
                                                    <th>脚本</th>
                                                    <th>备注</th>
                                                    <th>创建时间</th>
                                                    <th>更新时间</th>
                                                    <th>操作</th>
                                                </tr>
                                                </thead>
                                            </table>
                                        </div>
                                        <!-- /.card-body -->
                                    </div>
                                    <!-- /.card -->
                                </div>
                                <!-- /.col -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.container-fluid -->
        </section>
        <!-- /.content -->
    </div>

    <!-- 删除确认框 -->
    <div id="confirmDeleteModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">确认删除</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="recordIdInput" value="">
                    <p>确定要删除该记录吗？</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-danger" id="delButton">删除</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-light" style="width: 50%;">
        <!-- 控制侧边栏的按钮 -->
        <div class="p-3" style="display: flex; align-items: center;">
            <h5 style="flex-grow: 1;">数据源管理</h5>
            <button type="button" class="btn btn-sm" id="closeSidebar" data-slide="false" onclick="showConfirmationModal(event)">
                <i class="fas fa-times"></i> <!-- 关闭按钮图标 -->
            </button>
        </div>
        <div class="modal-body">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="name">名称</label>
                        <input id="name" type="text" class="form-control" style="height: 38px;">
                        <span id="classNameError1" class="text-danger"></span>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="selectServiceName">数据源</label>
                        <select class="form-control custom-select" id="selectServiceName" style="height: 38px;"></select>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <label for="remarksInput">脚本</label>
                <textarea id="script" class="form-control"></textarea>
            </div>

            <div class="form-group">
                <label for="remarksInput">备注</label>
                <textarea id="remarks" class="form-control"></textarea>
            </div>
            <!-- 使用 CSS 类来隐藏元素 -->
            <div class="form-group hidden-element">
                <label for="hiddenElement">要隐藏的元素</label>
                <input id="id" type="text" class="form-control" style="height: 38px;">
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-primary" onclick="saveDatasource(event)">保存</button>
<#--            <button type="button" class="btn btn-primary" onclick="test(event)">连通性测试</button>-->
        </div>
    </aside>

    <div class="modal fade" id="confirmationModal" tabindex="-1" role="dialog" aria-labelledby="confirmationModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document" style="margin-top: 250px;">
            <div class="modal-content">
                <div class="modal-body">
                    是否关闭侧边栏？
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-outline-primary" id="confirmCloseSidebar">确认</button>
                </div>
            </div>
        </div>
    </div>
    <!-- footer -->
    <@netCommon.commonFooter />

</div>
<!-- ./wrapper -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/js/dataset.js"></script>
</body>
</html>
