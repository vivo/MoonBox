<!--
Copyright 2022 vivo Communication Technology Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<template>
  <div v-loading="loading">
    <div>
      <el-form :model="ruleForm" :rules="rules" label-width="160px" ref="ruleForm" class="demo-ruleForm">
        <el-card>
          <div slot="header" class="clearfix">
            <h4>录制任务基础信息配置</h4>
          </div>
          <div>
            <el-form-item label="任务编号：" v-if="type != 'add'">
              {{ templateId }}
            </el-form-item>
            <el-form-item label="任务名称：" prop="templateName">
              <el-input v-model="ruleForm.templateName" placeholder="请输入任务名称" :disabled="disabledFlag" show-word-limit size="small"></el-input>
            </el-form-item>
            <el-form-item label="应用名称：" prop="appName">
              <el-select v-model="ruleForm.appName" filterable allow-create placeholder="请选择" :disabled="disabledFlag" size="small">
                <el-option v-for="item in appNameList" :key="item" :label="item" :value="item"></el-option>
              </el-select>
              <span class="tips red">*应用jvm启动参数需要包括该应用标识，否则无法attach到正确进程上</span>
            </el-form-item>
            <el-form-item label="单接口采集量：" prop="recordCount">
              <el-input-number v-model="ruleForm.recordCount" :step="1" :step-strictly="true" :min="1" :max="10000" placeholder="单接口采集流量条数，最大限制10000条" :disabled="disabledFlag" size="small"></el-input-number>
              <el-tooltip placement="right" style="margin-left: 10px">
                <div slot="content" class="tips-content text-tips-content">
                  <div class="rule-box">
                    单接口采集量范围1-10000。
                  </div>
                </div>
                <i class="el-icon-question" />
              </el-tooltip>
            </el-form-item>
            <el-form-item label="任务执行时长(min)：" prop="recordTaskDuration">
              <el-input-number v-model="ruleForm.recordTaskDuration" :step="1" :step-strictly="true" :min="60" :max="3600" placeholder="请输入任务执行时长" :disabled="disabledFlag" size="small"></el-input-number>
              <el-tooltip placement="right" style="margin-left: 10px">
                <div slot="content" class="tips-content text-tips-content">
                  <div class="rule-box">
                    单任务运行时长，当采集运行时间超过该值会自动停止采集任务。
                  </div>
                </div>
                <i class="el-icon-question" />
              </el-tooltip>
            </el-form-item>
          </div>
        </el-card>
        <el-card>
          <div slot="header" class="clearfix">
            <h4>流量采集配置</h4>
          </div>
          <div>
            <el-row>
              <h5 style="float: left;">http接口</h5>
              <el-button-group class="fr" v-if="buttonFlag">
                <el-button type="primary" size="small" @click="addAll('http')" icon="el-icon-plus">全选</el-button>
                <el-button type="danger" size="small" @click="delAll('http')" icon="el-icon-delete">全删</el-button>
                <el-button type="success" size="small" @click="deal('http', true)" icon="el-icon-document-add">新增</el-button>
              </el-button-group>
            </el-row>
            <el-table :data="ruleForm.httpRecordInterfaces" :header-cell-style="tableHeaderStyle" :cell-class-name="cellClassName" border size="small" max-height="515">
              <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
              <el-table-column label="http uri" prop="uri" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="描述" prop="desc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="采样率(采样率万分之N)" prop="sampleRate" width="200" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="操作" align="center" v-if="buttonFlag" width="150">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="deal('http', false, scope.$index, scope.row)">编辑</el-button>
                  <el-button type="text" size="mini" class="red" @click="del('http', scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <br>
            <el-row>
              <h5 style="float: left;">dubbo接口</h5>
              <el-button-group class="fr" v-if="buttonFlag">
                <el-button type="primary" size="small" @click="addAll('dubbo')" icon="el-icon-plus">全选</el-button>
                <el-button type="danger" size="small" @click="delAll('dubbo')" icon="el-icon-delete">全删</el-button>
                <el-button type="success" size="small" @click="deal('dubbo', true)" icon="el-icon-document-add">新增</el-button>
              </el-button-group>
            </el-row>
            <el-table :data="ruleForm.dubboRecordInterfaces" :header-cell-style="tableHeaderStyle" :cell-class-name="cellClassName" border size="small" max-height="515">
              <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
              <el-table-column label="接口名称" prop="interfaceName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="接口方法" prop="methodName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="描述" prop="desc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="采样率(采样率万分之N)" prop="sampleRate" width="200" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="操作" align="center" v-if="buttonFlag" width="150">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="deal('dubbo', false, scope.$index, scope.row)">编辑</el-button>
                  <el-button type="text" size="mini" class="red" @click="del('dubbo', scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <br>
            <el-row>
              <h5 style="float: left;">java方法</h5>
              <el-button-group class="fr" v-if="buttonFlag" >
                <el-button type="danger" size="small" @click="delAll('java')" icon="el-icon-delete">全删</el-button>
                <el-button type="success" size="small" @click="deal('java', true)" icon="el-icon-document-add">新增</el-button>
              </el-button-group>
            </el-row>
            <el-table :data="this.ruleForm.javaRecordInterfaces" :header-cell-style="tableHeaderStyle" :cell-class-name="cellClassName" border size="small" max-height="515">
              <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
              <el-table-column label="java类" prop="classPattern" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="java方法" prop="methodPatterns" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="描述" prop="desc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="采样率(采样率万分之N)" prop="sampleRate" width="200" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
              <el-table-column label="操作" align="center" v-if="buttonFlag" width="150">
                <template slot-scope="scope">
                  <el-button type="text" size="mini" @click="deal('java', false, scope.$index, scope.row)">编辑</el-button>
                  <el-button type="text" size="mini" class="red" @click="del('java', scope.$index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <br>
             <el-row>
                          <h5 style="float: left;">motan接口</h5>
                          <el-button-group class="fr" v-if="buttonFlag">
                            <el-button type="primary" size="small" @click="addAll('motan')" icon="el-icon-plus">全选</el-button>
                            <el-button type="danger" size="small" @click="delAll('motan')" icon="el-icon-delete">全删</el-button>
                            <el-button type="success" size="small" @click="deal('motan', true)" icon="el-icon-document-add">新增</el-button>
                          </el-button-group>
                        </el-row>
                        <el-table :data="ruleForm.motanRecordInterfaces" :header-cell-style="tableHeaderStyle" :cell-class-name="cellClassName" border size="small" max-height="515">
                          <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>
                          <el-table-column label="接口名称" prop="interfaceName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                          <el-table-column label="接口方法" prop="methodName" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                          <el-table-column label="描述" prop="desc" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                          <el-table-column label="采样率(采样率万分之N)" prop="sampleRate" width="200" align="center" show-overflow-tooltip :formatter="formatTableTDBlank"/>
                          <el-table-column label="操作" align="center" v-if="buttonFlag" width="150">
                            <template slot-scope="scope">
                              <el-button type="text" size="mini" @click="deal('motan', false, scope.$index, scope.row)">编辑</el-button>
                              <el-button type="text" size="mini" class="red" @click="del('motan', scope.$index)">删除</el-button>
                            </template>
                          </el-table-column>
                        </el-table>
          </div>
        </el-card>

        <!-- 高级选项卡片 -->
        <el-card>
          <div slot="header" class="clearfix">
            <h4 style="display: inline-block; marginRight: 10px;">高级选项</h4>
            <el-checkbox v-model="advanceOptionCheckFlag" @change="changeOption('statusOption', arguments[0])"/>
          </div>
          <el-form v-if="statusOption" class="demo-form-inline">
            <el-form-item label-width="160px">
              <template slot="label">
                <el-checkbox v-model="chooseAll" @change="chooseToggle" class="chooseAll" :disabled="disabledFlag" style="marginRight: 30px"></el-checkbox>
                <el-tooltip placement="top">
                  <div slot="content">
                    1、开启插件后，插件会自动拦截流量请求调用入参和出参情况<br/>
                    2、绿色代表入口插件，蓝色代表子调用插件
                  </div>
                  <i class="el-icon-question" />
                </el-tooltip>
                请选择插件：
              </template>
              <el-checkbox-group v-model="ruleForm.subInvocationPlugins" :disabled="disabledFlag" class="subInvocationPlugins">
                <template v-for="item in plugins">
                  <el-checkbox
                    :label="item.name"
                    :key="item.name"
                    :class="{ 'isGreen': (item.name == 'http' || item.name == 'dubbo-provider' || item.name == 'java-entrance'|| item.name == 'motan-provider') && pluginIsChecked(item.name) }">
                    <span :class="{ 'isGreen': item.name == 'http' || item.name == 'dubbo-provider' || item.name == 'java-entrance' || item.name == 'motan-provider'}">{{item.name}}</span>
                  </el-checkbox>
                </template>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label-width="160px">
              <template slot="label">
                <el-tooltip placement="top" content="sandbox日志会出输出到业务项目日志中心">
                  <i class="el-icon-question" />
                </el-tooltip>
                sandbox日志级别：
              </template>
              <el-select v-model="ruleForm.sandboxLogLevel" placeholder="请选择" size="small" :disabled="disabledFlag">
                <el-option v-for="item in logOptions" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label-width="160px">
              <template slot="label">
                <el-tooltip placement="top" content="repeater日志会出输出到业务项目日志中心">
                  <i class="el-icon-question" />
                </el-tooltip>
                repeater日志级别：
              </template>
              <el-select v-model="ruleForm.repeaterLogLevel" placeholder="请选择" size="small" :disabled="disabledFlag">
                <el-option v-for="item in logOptions" :key="item" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </el-card>
      </el-form>

      <!-- 粘性布局的按钮 -->
      <el-card class="add-record-bottom-component">
        <el-button size="small" type="primary" v-if="buttonFlag" @click="saveRecord('ruleForm')">保存</el-button>
        <el-button size="small" @click="resetForm()">关闭</el-button>
      </el-card>
    </div>

    <!-- http接口新增弹出框 -->
    <el-dialog title="http接口" :visible.sync="httpDialogFormVisible" width="600px">
      <el-form :model="httpRuleForm" :rules="httpRules" ref="http" class="demo-ruleForm" label-width="180px">
        <el-form-item label="http uri" prop="uri">
          <el-input v-model="httpRuleForm.uri" size="small"></el-input>
          <el-tooltip placement="top" style="margin-left: 1%">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                请输入要采集的http uri列表<br>uri不需要包括域名并且不支持正则表达式
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input type="textarea" :rows="3" v-model="httpRuleForm.desc"></el-input>
        </el-form-item>
        <el-form-item label="采样率(采样率万分之N)" prop="sampleRate">
          <el-input-number v-model="httpRuleForm.sampleRate" :min="1" :max="10000" label="1~10000" size="small"></el-input-number>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                采样率范围1-10000，10000代表全量采集
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
      </el-form>
      <div style="text-align:right;margin-top: 3%">
        <el-button @click="httpDialogFormVisible = false" size="small">取 消</el-button>
        <el-button type="primary" @click="dialogConfirm('http')" size="small">确 定</el-button>
      </div>
    </el-dialog>

    <!-- dubbo接口新增弹出框 -->
    <el-dialog title="dubbo接口" :visible.sync="dubboDialogFormVisible" width="600px">
      <el-form :model="dubboRuleForm" :rules="dubboRules" ref="dubbo" class="demo-ruleForm" label-width="180px">
        <el-form-item label="接口名称" prop="interfaceName">
          <el-input v-model="dubboRuleForm.interfaceName" size="small"></el-input>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                请输入要采集的dubbo接口名，支持正则表达式。<br>
                采集某个具体接口：com.test.query.TestRecordApi、采集所有接口：.*
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="接口方法" prop="methodName">
          <el-input v-model="dubboRuleForm.methodName" size="small"></el-input>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                请输入要采集的dubbo接口方法名，支持正则表达式。<br>
                采集某个具体方法名：testRecord、采集所有方法：.*
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input type="textarea" :rows="3" v-model="dubboRuleForm.desc" size="small"></el-input>
        </el-form-item>
        <el-form-item label="采样率(采样率万分之N)" prop="sampleRate">
          <el-input-number v-model="dubboRuleForm.sampleRate" :min="1" :max="10000" label="1~10000" size="small"></el-input-number>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                采样率范围1-10000，10000代表全量采集
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
      </el-form>
      <div style="text-align:right;margin-top: 3%">
        <el-button @click="dubboDialogFormVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="dialogConfirm('dubbo')" size="mini">确 定</el-button>
      </div>
    </el-dialog>

    <!-- java接口新增弹出框 -->
    <el-dialog title="java方法" class="javaDialogFormVisible" :visible.sync="javaDialogFormVisible" width="600px">
      <el-form :model="javaRuleForm" :rules="javaRules" ref="java" class="demo-ruleForm" label-width="180px">
        <el-form-item label="java类" prop="classPattern">
          <el-input v-model="javaRuleForm.classPattern" size="small" placeholder="com.test.TestClass"></el-input>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                请输入要采集的java类，例：com.test.TestClass
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="java方法" prop="methodPatterns">
          <el-input v-model="javaRuleForm.methodPatterns" size="small" placeholder="test1;test2;test3"></el-input>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                请输入要采集的java方法名，例：testRecord，目前仅支持无参、无返回值方法
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
        <el-form-item label="获取容器上下文的方法" prop="obtainApplicationContextMethod">
          <el-input v-model="javaRuleForm.obtainApplicationContextMethod" size="small" placeholder="com.test.SpringUtils.getApplicationContext"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input type="textarea" maxlength="100" :rows="3" v-model="javaRuleForm.desc" size="small"></el-input>
        </el-form-item>
        <el-form-item label="采样率" prop="sampleRate">
          <el-input-number v-model="javaRuleForm.sampleRate" :min="1" :max="10000" label="1~10000" size="small"></el-input-number>
          <el-tooltip placement="top" style="margin-left: 5px">
            <div slot="content" class="tips-content text-tips-content">
              <div class="rule-box">
                采样率范围1-10000，10000代表全量采集
              </div>
            </div>
            <i class="el-icon-question" />
          </el-tooltip>
        </el-form-item>
      </el-form>
      <div style="text-align:right;margin-top: 10px">
        <el-button @click="javaDialogFormVisible = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="dialogConfirm('java')" size="mini">确 定</el-button>
      </div>
    </el-dialog>

    <!-- motan接口新增弹出框 -->
        <el-dialog title="motan接口" :visible.sync="motanDialogFormVisible" width="600px">
          <el-form :model="motanRuleForm" :rules="motanRules" ref="motan" class="demo-ruleForm" label-width="180px">
            <el-form-item label="接口名称" prop="interfaceName">
              <el-input v-model="motanRuleForm.interfaceName" size="small"></el-input>
              <el-tooltip placement="top" style="margin-left: 5px">
                <div slot="content" class="tips-content text-tips-content">
                  <div class="rule-box">
                    请输入要采集的motan接口名，支持正则表达式。<br>
                    采集某个具体接口：com.test.query.TestRecordApi、采集所有接口：.*
                  </div>
                </div>
                <i class="el-icon-question" />
              </el-tooltip>
            </el-form-item>
            <el-form-item label="接口方法" prop="methodName">
              <el-input v-model="motanRuleForm.methodName" size="small"></el-input>
              <el-tooltip placement="top" style="margin-left: 5px">
                <div slot="content" class="tips-content text-tips-content">
                  <div class="rule-box">
                    请输入要采集的motan接口方法名，支持正则表达式。<br>
                    采集某个具体方法名：testRecord、采集所有方法：.*
                  </div>
                </div>
                <i class="el-icon-question" />
              </el-tooltip>
            </el-form-item>
            <el-form-item label="描述" prop="desc">
              <el-input type="textarea" :rows="3" v-model="motanRuleForm.desc" size="small"></el-input>
            </el-form-item>
            <el-form-item label="采样率(采样率万分之N)" prop="sampleRate">
              <el-input-number v-model="motanRuleForm.sampleRate" :min="1" :max="10000" label="1~10000" size="small"></el-input-number>
              <el-tooltip placement="top" style="margin-left: 5px">
                <div slot="content" class="tips-content text-tips-content">
                  <div class="rule-box">
                    采样率范围1-10000，10000代表全量采集
                  </div>
                </div>
                <i class="el-icon-question" />
              </el-tooltip>
            </el-form-item>
          </el-form>
          <div style="text-align:right;margin-top: 3%">
            <el-button @click="motanDialogFormVisible = false" size="mini">取 消</el-button>
            <el-button type="primary" @click="dialogConfirm('motan')" size="mini">确 定</el-button>
          </div>
        </el-dialog>

    <el-dialog
      title="提示"
      :visible.sync="addDialogVisible"
      width="500px">
      请输入采样率：
      <el-input-number
        v-model="dialogSampleRate"
        :min="1"
        :max="10000"
        :step="1"
        step-strictly
        placeholder="1~10000"
        size="small">
      </el-input-number>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="addDialogVisible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="addAllConfirm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import API from '@/api'
export default {
  data() {
    return {
      loading: false,
      appNameList: [],
      timeout:  null,
      templateId: '',
      plugins: [],
      isAdd: true,
      index: 0, //每张表所需要修改的索引号
      httpDialogFormVisible: false,
      dubboDialogFormVisible: false,
      motanDialogFormVisible: false,
      javaDialogFormVisible: false,
      type: 'add',
      chooseAll: true,
      ruleForm: {
        templateName: '',
        appName: '',
        recordCount: 500,
        recordTaskDuration: 60,
        httpRecordInterfaces: [],
        dubboRecordInterfaces: [],
        motanRecordInterfaces: [],
        javaRecordInterfaces: [],
        rmqRecordInterfaces: [],
        subInvocationPlugins: [],
        sandboxLogLevel: 'OFF',
        repeaterLogLevel: 'OFF'
      },
      rules: {
        templateName: [
          { required: true, message: '请输入任务名称', trigger: 'blur' },
        ],
        appName: [
          { required: true, message: '请输入或选择应用名称', trigger: 'blur' },
        ],
        recordCount: [
          { required: true, message: '请输入单接口采集量', trigger: 'blur' },
        ],
        recordTaskDuration: [
          { required: true, message: '请输入任务执行时长', trigger: 'blur' },
          { type: 'number', message: '执行时长必须为数字值' },
        ],
      },
      httpRuleForm: {
        uri: '',
        desc: '',
        sampleRate: ''
      },
      httpRules: {
        uri: [{ required: true, message: '请输入http uri', trigger: 'blur' }],
        desc: [
          { required: true, message: '请输入描述', trigger: 'blur' },
          {
            min: 1,
            max: 200,
            message: '长度在 200 个字符以内',
            trigger: 'blur',
          },
        ],
        sampleRate: [
          { required: true, message: '请输入采样率', trigger: 'blur' }
        ],
      },
      dubboRuleForm: {
        interfaceName: '',
        methodName: '',
        desc: '',
        sampleRate: ''
      },
      dubboRules: {
        interfaceName: [
          { required: true, message: '请输入接口名称', trigger: 'blur' },
          {
            min: 1,
            max: 350,
            message: '长度在 1 到 350 个字符',
            trigger: 'blur',
          },
        ],
        methodName: [
          { required: true, message: '请输入接口方法', trigger: 'blur' },
          {
            min: 1,
            max: 50,
            message: '长度在 1 到 50 个字符',
            trigger: 'blur',
          },
        ],
        desc: [
          { required: true, message: '请输入描述', trigger: 'blur' },
          {
            min: 1,
            max: 200,
            message: '长度在 200 个字符以内',
            trigger: 'blur',
          },
        ],
        sampleRate: [
          { required: true, message: '请输入采样率', trigger: 'blur' }
        ],
      },
       motanRuleForm: {
              interfaceName: '',
              methodName: '',
              desc: '',
              sampleRate: ''
            },
            motanRules: {
              interfaceName: [
                { required: true, message: '请输入接口名称', trigger: 'blur' },
                {
                  min: 1,
                  max: 350,
                  message: '长度在 1 到 350 个字符',
                  trigger: 'blur',
                },
              ],
              methodName: [
                { required: true, message: '请输入接口方法', trigger: 'blur' },
                {
                  min: 1,
                  max: 50,
                  message: '长度在 1 到 50 个字符',
                  trigger: 'blur',
                },
              ],
              desc: [
                { required: true, message: '请输入描述', trigger: 'blur' },
                {
                  min: 1,
                  max: 200,
                  message: '长度在 200 个字符以内',
                  trigger: 'blur',
                },
              ],
              sampleRate: [
                { required: true, message: '请输入采样率', trigger: 'blur' }
              ],
            },
      javaRuleForm: {
        classPattern: '',
        methodPatterns: '',
        obtainApplicationContextMethod: '',
        desc: '',
        sampleRate: ''
      },
      javaRules: {
        desc: [
          { required: true, message: '请输入描述', trigger: 'blur' },
          { min: 1, max: 200, message: '长度在 200 个字符以内', trigger: 'blur' }
        ],
        sampleRate: [
          { required: true, message: '请输入采样率', trigger: 'blur' }
        ]
      },
      advanceOptionCheckFlag: true,
      statusOption: true,
      disabledFlag: false,
      buttonFlag: true,
      addType: '',
      addDialogVisible: false,
      dialogSampleRate: undefined,
      logOptions: [
        'TRACE',
        'DEBUG',
        'INFO',
        'WARN',
        'ERROR',
        'FATAL',
        'OFF'
      ]
    }
  },
  computed: {
    pluginIsChecked () {
      return function (plugin) {
        return this.ruleForm.subInvocationPlugins.some(item => item === plugin)
      }
    }
  },
  watch: {
    'ruleForm.subInvocationPlugins': {
      handler (v) {
        this.chooseAll = v.length === this.plugins.length
      },
      immediate: true
    }
  },
  mounted() {
    this.type = this.$route.params.type
    this.id = this.$route.params.id
    this.templateId = this.$route.params.templateId
    // 获取应用选项
    this.getAppNameList()
    // 获取插件选项
    this.getPlugins()

    if (this.type == 'detail') {
      this.disabledDetail()
      this.getDetail()
    } else if (this.type == 'edit') {
      this.getDetail()
    }
  },
  methods: {
    getAppNameList() {
      API.record.getAppNameList().then((res) => {
        if (res.code === 'SUCCESS') {
          this.appNameList = res.data || []
        } else {
          this.$message.error('获取应用名称选项失败:' + res.msg)
        }
      })
    },
    getDetail() {
      this.loading = true
      API.record.recordDetail({ id: this.id }).then((res) => {
        if (res.code === 'SUCCESS') {
          this.ruleForm.appName = res.data.appName
          this.ruleForm.templateName = res.data.templateName
          let obj = JSON.parse(res.data.templateConfig)
          this.ruleForm.recordCount = obj.recordCount
          this.ruleForm.recordTaskDuration = obj.recordTaskDuration
          this.ruleForm.httpRecordInterfaces = obj.httpRecordInterfaces
          this.ruleForm.dubboRecordInterfaces = obj.dubboRecordInterfaces
          this.ruleForm.motanRecordInterfaces = obj.motanRecordInterfaces
          this.ruleForm.javaRecordInterfaces = []
          if (obj.javaRecordInterfaces && obj.javaRecordInterfaces.length) {
            obj.javaRecordInterfaces.forEach(item => {
              item.methodPatterns = item.methodPatterns.join(';')
            })
            this.ruleForm.javaRecordInterfaces = obj.javaRecordInterfaces
          }
          this.ruleForm.subInvocationPlugins = obj.subInvocationPlugins
          this.ruleForm.sandboxLogLevel = obj.sandboxLogLevel
          this.ruleForm.repeaterLogLevel = obj.repeaterLogLevel
        } else {
          this.$message.error('获取录制任务详情失败:' + res.msg)
        }
        this.loading = false
      })
    },
    getPlugins() {
      API.record.getPlugins().then((res) => {
        if (res.code === 'SUCCESS') {
          this.plugins = res.data
          if (this.type == 'add') {
            this.ruleForm.subInvocationPlugins = res.data.map(item => {
              if (item.checked) {
                return item.name
              }
            })
          }
        } else {
          this.$message.error('获取录制任务详情失败:' + res.msg)
        }
      })
    },
    addAll (addType) {
      this.addType = addType
      this.dialogSampleRate = undefined
      this.addDialogVisible = true
    },
    addAllConfirm () {
      if (!this.dialogSampleRate) return this.$message.warning('请输入采样率')
      this.ruleForm[`${this.addType}RecordInterfaces`].forEach(item => {
        item.sampleRate = this.dialogSampleRate
      })
      this.addDialogVisible = false
    },
    delAll (type) {
      this.$confirm(`此操作将删除${type}接口表格中的所有配置, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.ruleForm[`${type}RecordInterfaces`] = []
      }).catch(() => {
        this.$message({ type: 'info', message: '已取消删除' })
      })
    },
    // 新增/编辑 弹层
    deal(type, isAdd, index, row) {
      this.isAdd = isAdd
      if (isAdd) {
        this[`${type}RuleForm`] = {}
      } else {
        this.index = index
        this[`${type}RuleForm`] = JSON.parse(JSON.stringify(row))
      }
      this[`${type}DialogFormVisible`] = true
      this.$nextTick(() => {
        this.$refs[type].clearValidate()
      })
    },
    // 新增/编辑 确认
    dialogConfirm(formName) {
      const data = {
        http: {
          uri: this.httpRuleForm.uri.toString(),
          desc: this.httpRuleForm.desc,
          sampleRate: this.httpRuleForm.sampleRate
        },
        dubbo: {
          interfaceName: this.dubboRuleForm.interfaceName,
          methodName: this.dubboRuleForm.methodName,
          desc: this.dubboRuleForm.desc,
          sampleRate: this.dubboRuleForm.sampleRate
        },
            motan: {
                  interfaceName: this.motanRuleForm.interfaceName,
                  methodName: this.motanRuleForm.methodName,
                  desc: this.motanRuleForm.desc,
                  sampleRate: this.motanRuleForm.sampleRate
                },
        java: {
          classPattern: this.javaRuleForm.classPattern,
          methodPatterns: this.javaRuleForm.methodPatterns,
          obtainApplicationContextMethod: this.javaRuleForm.obtainApplicationContextMethod,
          desc: this.javaRuleForm.desc,
          sampleRate: this.javaRuleForm.sampleRate,
        }
      }
      this.$refs[formName].validate((valid) => {
        if (valid) {
          if (this.isAdd) {
            this.ruleForm[`${formName}RecordInterfaces`].push(data[formName])
          } else {
            this.$set(this.ruleForm[`${formName}RecordInterfaces`], this.index, data[formName])
          }
          this[`${formName}DialogFormVisible`] = false
        }  else {
          return false
        }
      })
    },
    // 删除
    del(type, index) {
      this.ruleForm[`${type}RecordInterfaces`].splice(index, 1)
    },
    disabledDetail() {
      this.disabledFlag = true
      this.buttonFlag = false
    },
    changeOption(type, value) {
      this[type] = value
    },
    chooseToggle(val) {
      if (val) {
        this.ruleForm.subInvocationPlugins = JSON.parse(JSON.stringify(this.plugins))
      } else {
        this.ruleForm.subInvocationPlugins = []
      }
    },
      cellClassName ({ row, column }) {
      if (!row.desc && column.property === 'desc') {
        return 'noDesc'
      }
    },
    saveRecord(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          let params = {}
          params.appName = 'moonbox'
          params.templateName = this.ruleForm.templateName
          params.appName = this.ruleForm.appName
          let templateConfig = {}
          templateConfig.dubboRecordInterfaces = this.ruleForm.dubboRecordInterfaces
          templateConfig.motanRecordInterfaces = this.ruleForm.motanRecordInterfaces
          templateConfig.httpRecordInterfaces = this.ruleForm.httpRecordInterfaces
          if (this.ruleForm.javaRecordInterfaces && this.ruleForm.javaRecordInterfaces.length) {
            this.ruleForm.javaRecordInterfaces.forEach(item => {
              item.methodPatterns = item.methodPatterns.split(';')
            }) 
          }
          templateConfig.javaRecordInterfaces = this.ruleForm.javaRecordInterfaces
          if (templateConfig.dubboRecordInterfaces.length === 0 &&templateConfig.motanRecordInterfaces.length === 0 && templateConfig.httpRecordInterfaces.length === 0 && templateConfig.javaRecordInterfaces.length === 0) return this.$message.warning('至少需要有一条入口流量采集配置')

          templateConfig.recordCount = this.ruleForm.recordCount
          templateConfig.recordTaskDuration = this.ruleForm.recordTaskDuration
          templateConfig.subInvocationPlugins = this.ruleForm.subInvocationPlugins
          templateConfig.sandboxLogLevel = this.ruleForm.sandboxLogLevel
          templateConfig.repeaterLogLevel = this.ruleForm.repeaterLogLevel
          params.templateConfig = JSON.stringify(templateConfig)
          if (this.type == 'add') {
            API.record.addRecord(params).then((res) => {
              if (res.code === 'SUCCESS') {
                this.resetForm()
              } else {
                this.$message.error('添加录制任务失败:' + res.msg)
              }
            })
          } else if (this.type == 'edit') {
            params.id = this.id
            API.record.update(params).then((res) => {
              if (res.code === 'SUCCESS') {
                this.resetForm()
              } else {
                this.$message.error('修改失败:' + res.msg)
              }
            })
          }
        } else {
          return false
        }
      })
    },
    resetForm() {
      this.$store.dispatch('page/close', {
        tagName: this.$route.name,
        notNext: true,
      })
      this.routerPush({ name: 'record', params: {}, query: {} })
    }
  }
}
</script>
<style lang="scss" scoped>
.demo-ruleForm {
  .el-form-item {
    margin-bottom: 15px;
  }
}
.el-row {
  height: 32px;
}

.tips {
  margin-left: 10px;
}

/deep/.el-dialog {
  .el-form-item {
    margin-bottom: 22px;
  }
}

.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all .3S linear;
}
.slide-fade-enter, .slide-fade-leave-to
/* .slide-fade-leave-active for below version 2.1.8 */ {
  transform: translateY(20px);
  opacity: 0;
}
.el-input.is-disabled .el-input__inner {
  background: #f2f6fc;
  opacity: 2;
  color: black;
}

.el-textarea.is-disabled .el-textarea__inner {
  background: #f2f6fc;
  opacity: 2;
  color: black;
}

.noDesc::before {
  content: '请点击编辑按钮添加描述信息';
  opacity: .4;
}

/deep/ .subInvocationPlugins {
  .isGreen{
      color: #67C23A;
    .el-checkbox__inner {
      border-color: #67C23A;
      background: #67C23A;
    }
  }
}
</style>
