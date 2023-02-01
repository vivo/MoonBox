/*
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
 */

module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: ['plugin:vue/essential'],
  globals: {
    // 设置CONFIG全局变量，并且是不可更改的
    CONFIG: false
  },
  rules: {
    // 不要求使用 === 和 !==
    eqeqeq: 0,
    // 警告在return、throw、continue 和 break 语句之后出现不可达代码
    'no-unreachable': 1,
    // 不要求箭头函数的参数使用圆括号
    'arrow-parens': 0,
    'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    complexity: ['warn', { max: 20 }]
  },
  parserOptions: {
    parser: 'babel-eslint'
  }
}
