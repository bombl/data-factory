<!DOCTYPE html>
<html lang="en">
<head>
    <#import "./common/common.ftl" as netCommon>
    <@netCommon.commonStyle />
    <style>
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

        #tabContainer table tbody tr {
            line-height: 1;
        }

        #tabContainer table {
            width: 100%;
            table-layout: auto;
        }

        #tabContainer table td, #tabContainer table th {
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }
        /* 自定义模态框样式 */
        #confirmationModal {
            align-items: center;
            justify-content: center
        }
        .modal-centered {
            display: flex;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
        }
        .modal-backdrop.show {
            opacity: 0.5 !important; /* 调整透明度 */
        }
        .table-container {
            max-height: 450px; /* 设置最大高度，根据需要调整 */
            overflow-y: auto; /* 启用垂直滚动条 */
        }
    </style>
    <title>DATA</title>
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
        <div class="content-header" style="padding-bottom: 0px;">
            <div class="container-fluid">
                <div class="row mb-2">
                </div><!-- /.row -->
            </div><!-- /.container-fluid -->
        </div>
        <!-- /.content-header -->
        <!-- Main content -->
        <section class="content" style="margin-top: 0px;">
            <div class="card">
                <div class="card-header">
                    <h1 class="card-title">数据工厂</h1>
                    <div class="card-tools">
                        <button id="generateData" onclick="generateData()" class="btn btn-outline-primary"
                                style="right: 13px;">
                            生成数据
                        </button>
                        <button type="button" class="btn btn-tool" data-card-widget="collapse" title="Collapse">
                            <i class="fas fa-minus" style="color:#007bff"></i>
                        </button>
                        <button type="button" class="btn btn-tool" data-card-widget="remove" title="Remove">
                            <i class="fas fa-times" style="color:#007bff"></i>
                        </button>
                        <button type="button" class="btn btn-tool" data-widget="control-sidebar" ata-slide="true" id="sidebarbtn">
                            <i class="fas fa-th-large" style="color:#007bff"></i>
                        </button>

                    </div>
                </div>
                <div class="card-body">
                    <form>
                        <div class="row">
                            <div class="col-sm-12">
                                <!-- textarea -->
                                <div class="form-group">
                                <textarea class="form-control" rows="3" id="sqlTextarea"
                                          placeholder="请输入SQL">select * from JS_YWDZ_ZS_JFXXCSJKDZ  jfxx
        LEFT JOIN WD_SBJBJGDZ dz ON jfxx.SBJBJG_DM =dz.SBJBJG_DM and dz.yxbz='Y' and dz.xybz='Y'
        where jfxx.SWJG_DM= '14512000000' AND jfxx.DZLX in ('0','1') AND jfxx.DZNY ='202308'
        AND dz.SBJBJG_DM IN ('100001','100002') and jfxx.SJQY = '1' and jfxx.XM_DM in ('0','1','2','3','4')</textarea>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="card">
                <!-- 添加选项卡容器 -->
                <div class="row" id="tabContainer" style="overflow-x: auto;min-width: 400px;">
                    <!-- 选项卡头部 -->
                    <ul class="nav nav-tabs" style="margin-left: 8px;" id="myTabs">
                        <!-- JavaScript 将动态生成选项卡头部 -->
                    </ul>

                    <!-- 选项卡内容 -->
                    <div class="tab-content" id="tab-content">
                        <!-- JavaScript 将动态生成选项卡内容 -->
                    </div>
                </div>
            </div>
            <!-- /.container-fluid -->
        </section>
    </div>
    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-light" style="width: 50%;">
        <!-- 控制侧边栏的按钮 -->
        <div class="p-3" style="display: flex; align-items: center;">
            <h5 style="flex-grow: 1;">配置</h5>
            <button type="button" class="btn btn-sm" id="closeSidebar" data-slide="false" onclick="showConfirmationModal(event)">
                <i class="fas fa-times"></i> <!-- 关闭按钮图标 -->
            </button>
        </div>
        <div class="modal-body">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="selectServiceName">数据源</label>
                        <select class="form-control custom-select" id="selectServiceName" style="height: 38px;"></select>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="generateNum">生成数量</label>
                        <input id="generateNum" type="text" class="form-control" style="height: 38px;" value="10">
                        <span id="classNameError1" class="text-danger"></span>
                    </div>
                </div>
            </div>
        </div>
        <!-- 标签页容器 -->
        <ul class="nav nav-tabs" id="tabs" role="tablist" style="margin-left: 16px;">
            <!-- 这里将动态添加标签页按钮 -->
        </ul>

        <!-- 标签页内容容器 -->
        <div class="tab-content" id="tabContent" style="margin-left: 16px;">
            <!-- 这里将动态添加标签页内容 -->
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

    <!-- /.control-sidebar -->
    <!-- footer -->
    <@netCommon.commonFooter />
</div>
<!-- ./wrapper -->
<@netCommon.commonScript />
<script src="${request.contextPath}/static/js/index.js"></script>
</body>
</html>
