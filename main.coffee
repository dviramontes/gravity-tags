url = 'http://feeds.delicious.com/v2/json/tags/dviramontes'
width = 400
height = 200

withData = (data) ->
  main data

$.ajax(url,
  crossDomain: true
  dataType: 'jsonp')
.error((xhr, status, error) ->
  alert(error.message))
.success(withData)

main = (data) ->
  tags = _.map data, (d, i) -> tag: i, "count": d

  theme = d3.scale.linear()
    .domain([1, 20])
    .range(['#67001f'
        '#b2182b'
        '#d6604d'
        '#f4a582'
        "#fddbc7"
        "#ffffff"
        "#e0e0e0"
        "#bababa"
        "#878787"
        "#4d4d4d"
        "#1a1a1a"])

  svg = d3.select('body')
  .append('svg')

  svg
    .attr 'width', width
    .attr 'height', height
    .style "border": "2px red solid"

  group = svg.append('g')
    .attr('transform', 'translate(' + 0 + ',' + height / 2 + ')')

  group
    .selectAll('circle')
    .data(tags)
    .enter()
    .append('circle')
    .attr('cx', (d, i) ->
      i)
    .attr('cy', 0)
    .attr('r', (d, i) ->
      d.count)
    .attr('fill', (d, i) ->
      theme(i))




