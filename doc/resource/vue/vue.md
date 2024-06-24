# vue cli

Vue CLI 现已处于维护模式!
现在官方推荐使用 create-vue 来创建基于 Vite 的新项目。另外请参考 Vue 3 工具链指南 以了解最新的工具推荐。

https://cli.vuejs.org/zh/guide/
起步
安装：
npm install -g @vue/cli
OR
yarn global add @vue/cli

创建一个项目：
vue create my-project
OR
vue ui

# element-plus
element-ui不支持vue3，所以有了element-plus

https://element-plus.org/zh-CN/guide/quickstart.html

完整引入#
如果你对打包后的文件大小不是很在乎，那么使用完整导入会更方便。

// main.ts
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)

app.use(ElementPlus)
app.mount('#app')

# vue代理跨域使用axios实现百度搜索功能
https://blog.csdn.net/m0_72189003/article/details/127443543

# Axios 跨域问题 No ‘Access-Control-Allow-Origin‘ header is present on the requested resource.
https://blog.csdn.net/qq_41992943/article/details/118544389

# axios
https://www.axios-http.cn/

# NProgress使用教程
https://www.cnblogs.com/d-lir/p/14018641.html

https://blog.csdn.net/CEZLZ/article/details/108198402

# vue官网
https://cn.vuejs.org/guide/quick-start.html
https://vue3js.cn/


# vuex官网
https://vuex.vuejs.org/zh/

# vue router
https://router.vuejs.org/zh/guide/essentials/dynamic-matching.html

# 防抖
Vue.directive('debounce', { //防抖函数指令
      inserted: function(el, binding) {
        let timer;
        el.addEventListener("click", () => {
          if (timer) {
            clearTimeout(timer);
          }
          timer = setTimeout(() => {
          //关键点:vue 的自定义指令传递的参数binding 如果是一个函数,则通过      binding.value()来执行,通过上述示例,还可以传递比如事件, 绑定对象之类的
            binding.value();
          }, 1000);
        });
      }
   })

# sessionStorage
export function setSessionStorage(name, value){
    window.sessionStorage.setItem(name, JSON.stringify(value))
}

# Moment.js
https://momentjs.cn/guides/


# pnpm的安装与使用
pnpm使用
官网： https://pnpm.js.org/installation/
全局安装
npm install pnpm -g

设置源
pnpm config get registry 
//切换淘宝源
pnpm config set registry http://registry.npm.taobao.org 


使用
pnpm install 包  // 
pnpm i 包
pnpm add 包    // -S  默认写入dependencies
pnpm add -D    // -D devDependencies
pnpm add -g    // 全局安装

移除
pnpm remove 包                            //移除包
pnpm remove 包 --global                   //移除全局包

更新
pnpm up                //更新所有依赖项
pnpm upgrade 包        //更新包
pnpm upgrade 包 --global   //更新全局包

设置存储路径
arduino复制代码pnpm config set store-dir /path/to/.pnpm-store

https://juejin.cn/post/7037480024106074148

不用切换源，安装好的registry.npmmirror.com就已经是淘宝源了
淘宝源地址已经换了

# 轻松解决 Node.js 的 Error: error:0308010C:digital envelope routines::unsupported 错误
export NODE_OPTIONS=--openssl-legacy-provider

# 新版镜像
npm config set registry https://registry.npmmirror.com

# （浅谈）npm 、cnpm、yarn 、pnpm、CDN之间的差异
https://juejin.cn/post/7096048182290972679