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

const gulp = require('gulp')
const zip = require('gulp-zip')
const md5 = require('gulp-md5-plus')
const fs = require('fs')
const zipName = 'moonbox'

// 打包zip
gulp.task('zip', () =>
  gulp.src(`./dist/**`)
    .pipe(zip(`./${zipName}.zip`))
    .pipe(gulp.dest('.'))
)

// 编码md5
gulp.task('md5', ['zip'], function () {
  return gulp.src(`./${zipName}.zip`)
    .pipe(md5(32, `${zipName}.zip`))
    .pipe(gulp.dest('./prod-md5-zip'))
})

// 执行任务
gulp.task('default', ['zip', 'md5'], () => {
  // 清理临时文件夹
  fs.unlinkSync(`./${zipName}.zip`)
  let files = fs.readdirSync('./prod-md5-zip')
  if (files && files.length === 1) {
    console.log(new Date().toLocaleString('zh', { hour12: false }) + ' 静态资源包MD5值：' + files[0].split('_')[1].replace('.zip', ''))
  } else {
    console.log('打包文件夹下文件数异常，请检查！')
  }
})
